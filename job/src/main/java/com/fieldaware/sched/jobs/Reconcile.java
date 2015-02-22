package com.fieldaware.sched.jobs;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.fieldaware.dao.ContractorDAO;
import com.fieldaware.dao.CostDAO;
import com.fieldaware.dao.CustomerDAO;
import com.fieldaware.dao.ItemDAO;
import com.fieldaware.dao.ItemTypeDAO;
import com.fieldaware.dao.PriceDAO;
import com.fieldaware.util.RestClient;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Reconcile extends QuartzJobBean{
	
	private static Logger logger = LoggerFactory.getLogger(Reconcile.class);
	
	@Autowired
	private RestClient restClient;
	
	@Autowired
	private CustomerDAO customerDAO;
	
	@Autowired
	private ItemDAO itemDAO;
	
	@Autowired
	private ItemTypeDAO itemTypeDAO;
	
	@Autowired
	private PriceDAO priceDAO;
	
	@Autowired
	private CostDAO costDAO;
	
	@Autowired
	private ContractorDAO contractorDAO;
	
	private List<String> jobIds = null;

	private Map<String, Long> custMap;

	private Map<String, Long> itemMap;

	private Map<Integer, Map<Integer, Double>> priceMap;

	private Map<Integer, Map<Integer, Double>> costMap;

	private Map<String, Long> contractorMap;
	
	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		restClient = (RestClient) context.getMergedJobDataMap().get("restClient");
		customerDAO = (CustomerDAO) context.getMergedJobDataMap().get("customerDAO");
		itemDAO	= (ItemDAO) context.getMergedJobDataMap().get("itemDAO");
		itemTypeDAO = (ItemTypeDAO) context.getMergedJobDataMap().get("itemTypeDAO");
		priceDAO = (PriceDAO) context.getMergedJobDataMap().get("priceDAO");
		costDAO= (CostDAO) context.getMergedJobDataMap().get("costDAO");
		contractorDAO= (ContractorDAO)context.getMergedJobDataMap().get("contractorDAO");
		
		getCompletedJobs();
		loadData();
		updateJobDetails("J99");
	}
	
	public RestClient getRestClient() {
		return restClient;
	}

	public void setRestClient(RestClient restClient) {
		this.restClient = restClient;
	}

	public List<String> getCompletedJobs(){
		JsonElement root;
		jobIds = new ArrayList<String>();
		try {
			root = restClient.getMethod("/?start=2014-12-06T16%300&end=2015-01-28T16%300&state=completed");
			Long count = root.getAsJsonObject().get("count").getAsLong();
			for(int i=0;i<count;i++){
				jobIds.add(root.getAsJsonObject().get("items").getAsJsonArray().get(i).getAsJsonObject().get(("jobId")).toString());
			}
		} catch (KeyManagementException e) {
			logger.error(e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage());
		} catch (KeyStoreException e) {
			logger.error(e.getMessage());
		} catch (ClientProtocolException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return jobIds;
	}
	
	public void loadData(){
		if(!jobIds.isEmpty()){
			custMap = customerDAO.getCustomer();
			itemMap = itemDAO.getItems();
			priceMap = priceDAO.getPrice();
			costMap = costDAO.getCost();
			contractorMap = contractorDAO.getContractors();
		}
	}
	
	public void updateJobDetails(String jobId){
		JsonElement root;
		Gson gson = new Gson();
		try {
			root = restClient.getMethod("/" +jobId);
			JsonArray tasks = root.getAsJsonObject().get("tasks").getAsJsonArray(); 
			JsonObject cust = root.getAsJsonObject().get("customer").getAsJsonObject(); 
			JsonObject cont = root.getAsJsonObject().get("jobLead").getAsJsonObject(); 
			
			boolean priceVerified = false;
			if(root.getAsJsonObject().get("customFields")
					.getAsJsonObject().get("8e47267e5629485eaa703cb06b3bcf79") != null){
			    priceVerified = root.getAsJsonObject().get("customFields")
					.getAsJsonObject().get("8e47267e5629485eaa703cb06b3bcf79").getAsBoolean(); 
			}
			
			if(priceVerified){
				logger.debug("*********  " +jobId + " price already verified ");
				return;
			}
			
			String cName = cust.get("name").toString().replaceAll("[^a-zA-Z0-9]+","");
			String contName = cont.get("emailAddress").toString().replaceAll("[^a-zA-Z0-9]+","");
			Integer cId = null;
			Integer contId = null;
			if(cName != null){
				cId = custMap.get(cName).intValue();
			}else{
				logger.debug("*********  " + jobId + " customer name is null ");
				return;
			}
			
			if(contName != null){
				if(contractorMap.get(contName) != null){
					contId = contractorMap.get(contName).intValue();
				}else{
					logger.debug("*********  " + jobId + " contractor name is null ");
					return;
				}
			}else{
				logger.debug("*********  " + jobId + " contractor name is null ");
				return;
			}
			
			int tasksCount = tasks.size();
			for(int t=0; t<tasksCount; t++){
				Double totalPrice = 0d;
				Double totalCost = 0d;
				String tName = tasks.get(t).getAsJsonObject().get("task").getAsJsonObject().get("name").toString();
				tName = tName.replaceAll("[^a-zA-Z]+","");
				if(tName == null){
					logger.debug("*********  " + jobId + " task name is null or no tasks ");
					continue;
				}
				
				JsonArray items = tasks.get(t).getAsJsonObject().get("items").getAsJsonArray();
				int itemCount = items.size();
				for(int i=0;i<itemCount; i++){
					String itemName = items.get(i).getAsJsonObject().get("item").getAsJsonObject().get("name").toString();
					Integer itemId = itemMap.get(itemName.replaceAll("[^a-zA-Z0-9]+","")).intValue();
					Double unitPrice = priceMap.get(cId).get(itemId);
					Double unitCost = costMap.get(contId).get(itemId);
					items.get(i).getAsJsonObject().add("unitPrice", gson.fromJson(unitPrice.toString(), JsonElement.class));
					items.get(i).getAsJsonObject().add("unitCost", gson.fromJson(unitCost.toString(), JsonElement.class));
					
					totalPrice = totalPrice + (items.get(i).getAsJsonObject().get("quantity").getAsDouble()
							* items.get(i).getAsJsonObject().get("unitPrice").getAsDouble());
					totalCost = totalCost + (items.get(i).getAsJsonObject().get("quantity").getAsDouble()
							* items.get(i).getAsJsonObject().get("unitCost").getAsDouble());
				}
				tasks.get(t).getAsJsonObject().add("unitPrice",gson.fromJson(totalPrice.toString(),JsonElement.class));
				tasks.get(t).getAsJsonObject().add("unitCost",gson.fromJson(totalCost.toString(),JsonElement.class));
			}
			
			root.getAsJsonObject().get("customFields").getAsJsonObject().addProperty("8e47267e5629485eaa703cb06b3bcf79", true);
			
			restClient.postMethod("/" +jobId, root.toString());
		} catch (KeyManagementException e) {
			logger.error(e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage());
		} catch (KeyStoreException e) {
			logger.error(e.getMessage());
		} catch (ClientProtocolException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	public CustomerDAO getCustomerDAO() {
		return customerDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public ItemDAO getItemDAO() {
		return itemDAO;
	}

	public void setItemDAO(ItemDAO itemDAO) {
		this.itemDAO = itemDAO;
	}

	public ItemTypeDAO getItemTypeDAO() {
		return itemTypeDAO;
	}

	public void setItemTypeDAO(ItemTypeDAO itemTypeDAO) {
		this.itemTypeDAO = itemTypeDAO;
	}

	public PriceDAO getPriceDAO() {
		return priceDAO;
	}

	public void setPriceDAO(PriceDAO priceDAO) {
		this.priceDAO = priceDAO;
	}

	public List<String> getJobIds() {
		return jobIds;
	}

	public void setJobIds(List<String> jobIds) {
		this.jobIds = jobIds;
	}

	public CostDAO getCostDAO() {
		return costDAO;
	}

	public void setCostDAO(CostDAO costDAO) {
		this.costDAO = costDAO;
	}

	public ContractorDAO getContractorDAO() {
		return contractorDAO;
	}

	public void setContractorDAO(ContractorDAO contractorDAO) {
		this.contractorDAO = contractorDAO;
	}

}