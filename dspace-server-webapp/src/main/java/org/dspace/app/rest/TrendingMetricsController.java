/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest;

import java.math.BigInteger;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.dspace.app.rest.enums.DateEnum;
import org.dspace.app.rest.enums.DocumentTypeEnum;
import org.dspace.app.rest.model.CountryDTO;
import org.dspace.app.rest.model.DocType;
import org.dspace.app.rest.model.HighChartmapDTO;
import org.dspace.app.rest.model.ItemBarchartDTO;
import org.dspace.app.rest.model.ItemBardataset;
import org.dspace.app.rest.model.ItemCartDTO;
import org.dspace.app.rest.model.LineChartDTO;
import org.dspace.app.rest.model.LineChartdataPointsDTO;
import org.dspace.app.rest.model.TrendingCommunitiesChartDTO;
import org.dspace.app.rest.utils.ContextUtil;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.content.service.CommunityService;
import org.dspace.content.service.EventTrackService;
import org.dspace.content.service.ItemService;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

@RestController
@RequestMapping(value = "/api/trendingMatrics")
public class TrendingMetricsController implements InitializingBean {
	@Autowired
	private DiscoverableEndpointsService discoverableEndpointsService;

	private static Logger log = LogManager.getLogger(TrendingMetricsController.class);

	@Autowired
	EventTrackService eventTrackService;

	@Autowired
	ItemService itemService;

	@Autowired
	CommunityService communityService;

	Gson gson = new Gson();

	public void afterPropertiesSet() throws Exception {
		this.discoverableEndpointsService.register(this,
				Arrays.asList(new Link[] { Link.of("/api/trendingMatrics", "trendingMatrics") }));
	}

	/***
	 * 
	 * @param params
	 * @param response
	 * @param request
	 * @return
	 */
	@GetMapping({ "/highChartmapDTO" })
	@Produces({ "application/json" })
	@Consumes({ "application/json" })
	public String highChartmapDTO(@RequestParam Map<String, String> params, HttpServletResponse response,
			HttpServletRequest request) {
		log.info("Inside TrendingMetricsController::highChartmapDTO() method...");

		int dateType = Integer.valueOf(params.get("dateType"));
		log.info("dateType in request is:", dateType);
		DateEnum dateMonthYear = DateEnum.getDeploymentClass(dateType);
		List<HighChartmapDTO> highChartMapList = new ArrayList<HighChartmapDTO>();

		if (params.get("collectionId") != null) {
			dateMonthYear.setCollectionList(params.get("collectionId"));
		} else {
			dateMonthYear.setCollectionList(Strings.EMPTY);
		}
		try {
			Context context = ContextUtil.obtainContext(request);
			EPerson user = context.getCurrentUser();
			if (context != null && null == user) {
				log.info("user not loggedin..");
			} else {
				log.info("loggedin user:", user);
			}
			List<Object[]> ItemMostvList = eventTrackService.getGeolocationData(context,
					dateMonthYear.getqueryString());
			for (Object[] iteamViewobjects : ItemMostvList) {
				HighChartmapDTO highChartmapDTO = new HighChartmapDTO();
				String county = iteamViewobjects[0].toString();
				int count = ((BigInteger) iteamViewobjects[3]).intValue();
				int Type = (int) iteamViewobjects[1];
				highChartmapDTO.setCountry(county);
				highChartMapList.add(highChartmapDTO);
			}
			context.complete();
		} catch (SQLException sqlExc) {
			log.error("SQLException occured :", sqlExc);
		} catch (Exception ex) {
			log.error("Exception occured :", ex);
		}
		log.info("Exiting from TrendingMetricsController::highChartmapDTO() method...");
		return gson.toJson(highChartMapList);
	}

	/***
	 * 
	 * @param params
	 * @param response
	 * @param request
	 * @return
	 */
	@GetMapping({ "/getTrendingCommunitie" })
	@Produces({ "application/json" })
	@Consumes({ "application/json" })
	public String getTrendingCommunitie(@RequestParam Map<String, String> params, HttpServletResponse response,
			HttpServletRequest request) {
		log.info("Inside TrendingMetricsController::getTrendingCommunitie() method...");

		int dateType = Integer.valueOf(params.get("dateType"));
		log.info("dateType in request is:", dateType);
		DateEnum dateMonthYear = DateEnum.getDeploymentClass(dateType);
		List<TrendingCommunitiesChartDTO> trendingCommunitiesChartList = new ArrayList<>();

		if (params.get("collectionId") != null) {
			dateMonthYear.setCollectionList(params.get("collectionId"));
		} else {
			dateMonthYear.setCollectionList(Strings.EMPTY);
		}
		try {
			Context context = ContextUtil.obtainContext(request);
			EPerson user = context.getCurrentUser();
			if (context != null && null == user) {
				log.info("user not loggedin..");
			} else {
				log.info("loggedin user:", user);
			}
			List<Object[]> ItemList = eventTrackService.getTrendingCommunitie(context, dateMonthYear.getqueryString(),
					"Cast(e.dspaceobjectid as varchar)");

			for (Object[] objects : ItemList) {
				UUID uid = UUID.fromString((String) objects[0]);
				Community community = communityService.find(context, uid);
				if (community != null) {
					int count = ((BigInteger) objects[1]).intValue();
					TrendingCommunitiesChartDTO trendingCommunitiesChart = new TrendingCommunitiesChartDTO();
					trendingCommunitiesChart.setY(count);
					trendingCommunitiesChart.setLabel(community.getName());
					trendingCommunitiesChart.setUuid(community.getID().toString());
					trendingCommunitiesChartList.add(trendingCommunitiesChart);
				}

			}
			context.complete();
		} catch (SQLException sqlExc) {
			log.error("SQLException occured :", sqlExc);
		} catch (Exception ex) {
			log.error("Exception occured :", ex);
		}
		log.info("Exiting from TrendingMetricsController::getTrendingCommunitie() method...");
		return gson.toJson(trendingCommunitiesChartList);
	}

	/***
	 * 
	 * @param params
	 * @param response
	 * @param request
	 * @return
	 */
	@GetMapping({ "/getTrandingType" })
	@Produces({ "application/json" })
	@Consumes({ "application/json" })
	public String getTrandingType(@RequestParam Map<String, String> params, HttpServletResponse response,
			HttpServletRequest request) {
		log.info("Inside TrendingMetricsController::getTrandingType() method...");

		int dateType = Integer.valueOf(params.get("dateType"));
		log.info("dateType in request is:", dateType);
		DateEnum dateMonthYear = DateEnum.getDeploymentClass(dateType);
		List<LineChartDTO> lineChartList = new ArrayList<>();

		if (params.get("collectionId") != null) {
			dateMonthYear.setCollectionList(params.get("collectionId"));
		} else {
			dateMonthYear.setCollectionList(Strings.EMPTY);
		}
		try {
			Context context = ContextUtil.obtainContext(request);
			EPerson user = context.getCurrentUser();
			if (context != null && null == user) {
				log.info("user not loggedin..");
			} else {
				log.info("loggedin user:", user);
			}
			Map<String, LineChartDTO> DocTypeMap = new HashMap<>();
			List<Object[]> ItemList = eventTrackService.itemLineChartAll(context, dateMonthYear.getqueryString(),
					"to_char(e.action_date,'dd-MM-YYYY')");

			for (DocumentTypeEnum t : DocumentTypeEnum.values()) {
				DocTypeMap.put(t.getName(), new LineChartDTO("spline", t.getName(), "#0.##",
						new ArrayList<LineChartdataPointsDTO>(), t.getColour()));
			}

			for (Object[] objects : ItemList) {
				int count = ((BigInteger) objects[2]).intValue();
				int Type = ((Integer) objects[1]).intValue();
				String date = ((Date) objects[0]).toString();
				DocumentTypeEnum DocumentTypeEnumobj = DocumentTypeEnum.getBykey(Type);
				LineChartdataPointsDTO BookDataDTO = new LineChartdataPointsDTO(date, count);
				DocTypeMap.get(DocumentTypeEnumobj.getName()).getDataPoints().add(BookDataDTO);

			}

			for (Map.Entry<String, LineChartDTO> entry : DocTypeMap.entrySet()) {
				if (entry.getValue().getDataPoints().size() != 0) {
					lineChartList.add(entry.getValue());
				}
			}
			context.complete();
		} catch (SQLException sqlExc) {
			log.error("SQLException occured :", sqlExc);
		} catch (Exception ex) {
			log.error("Exception occured :", ex);
		}
		log.info("Exiting from TrendingMetricsController::getTrendingCommunitie() method...");
		return gson.toJson(lineChartList);
	}

	/***
	 * 
	 * @param params
	 * @param response
	 * @param request
	 * @return
	 */
	@GetMapping({ "/getTrandingSearch" })
	@Produces({ "application/json" })
	@Consumes({ "application/json" })
	public String getTrandingSearch(@RequestParam Map<String, String> params, HttpServletResponse response,
			HttpServletRequest request) {
		log.info("Inside TrendingMetricsController::getTrandingSearch() method...");

		int dateType = Integer.valueOf(params.get("dateType"));
		log.info("dateType in request is:", dateType);
		DateEnum dateMonthYear = DateEnum.getDeploymentClass(dateType);
		List<TrendingCommunitiesChartDTO> trendingCommunitiesList = new ArrayList<>();

		if (params.get("collectionId") != null) {
			dateMonthYear.setCollectionList(params.get("collectionId"));
		} else {
			dateMonthYear.setCollectionList(Strings.EMPTY);
		}
		try {
			Context context = ContextUtil.obtainContext(request);
			EPerson user = context.getCurrentUser();
			if (context != null && null == user) {
				log.info("user not loggedin..");
			} else {
				log.info("loggedin user:", user);
			}
			List<Object[]> ItemList = eventTrackService.getTrendingSearch(context, dateMonthYear.getqueryString(),
					"e.title");
			for (Object[] objects : ItemList) {
				String title = (String) objects[0];
				int count = ((BigInteger) objects[1]).intValue();
				TrendingCommunitiesChartDTO trendingCommunitiesChart = new TrendingCommunitiesChartDTO();
				trendingCommunitiesChart.setY(count);
				trendingCommunitiesChart.setLabel(title);
				trendingCommunitiesChart.setUuid(title);
				trendingCommunitiesList.add(trendingCommunitiesChart);
			}
			context.complete();
		} catch (SQLException sqlExc) {
			log.error("SQLException occured :", sqlExc);
		} catch (Exception ex) {
			log.error("Exception occured :", ex);
		}
		log.info("Exiting from TrendingMetricsController::getTrandingSearch() method...");
		return gson.toJson(trendingCommunitiesList);
	}

	/***
	 * 
	 * @param params
	 * @param response
	 * @param request
	 * @return
	 */
	@GetMapping({ "/getTrendingSearchByDate" })
	@Produces({ "application/json" })
	@Consumes({ "application/json" })
	public String getTrendingSearchByDate(@RequestParam Map<String, String> params, HttpServletResponse response,
			HttpServletRequest request) {
		log.info("Inside TrendingMetricsController::getTrandingSearch() method...");

		int dateType = Integer.valueOf(params.get("dateType"));
		log.info("dateType in request is:", dateType);
		DateEnum dateMonthYear = DateEnum.getDeploymentClass(dateType);
		String searchvalue = params.get("searchvalue");
		log.info("searchvalue in request is:", searchvalue);

		if (params.get("collectionId") != null) {
			dateMonthYear.setCollectionList(params.get("collectionId"));
		} else {
			dateMonthYear.setCollectionList(Strings.EMPTY);
		}
		try {
			Context context = ContextUtil.obtainContext(request);
			EPerson user = context.getCurrentUser();
			if (context != null && null == user) {
				log.info("user not loggedin..");
			} else {
				log.info("loggedin user:", user);
			}
			if (StringUtils.isNotBlank(searchvalue)) {
				String search = Strings.EMPTY;
				if (!searchvalue.equals("ALL")) {
					search = "and title ='" + searchvalue + "'";
				}
				List<Object[]> iteamViewObjectList = eventTrackService.SearchBYDate(context,
						dateMonthYear.getqueryString() + search);
				ItemBarchartDTO itemBarchartviewDTO = new ItemBarchartDTO();
				itemBarchartviewDTO.setLable(request.getParameter("searchvalue").toString());
				ItemBardataset itemBardatasetView = new ItemBardataset("Search", "transparent");
				itemBardatasetView.setBorderWidth(3);
				itemBardatasetView.setPointRadius(2);
				itemBardatasetView.setPointBackgroundColor("#1F3F5E");
				itemBardatasetView.setBorderColor("#1F3F5E");

				for (Object[] iteamViewobjects : iteamViewObjectList) {
					SimpleDateFormat DateFor = new SimpleDateFormat("MMM dd yyyy");
					String stringDate = DateFor.format((Date) iteamViewobjects[0]);
					if (!itemBarchartviewDTO.getLabels().contains(stringDate)) {
						itemBarchartviewDTO.getLabels().add(stringDate);
					}
					int count = ((BigInteger) iteamViewobjects[1]).intValue();
					itemBarchartviewDTO.setCount(itemBarchartviewDTO.getCount() + count);
					itemBardatasetView.getData().add(count);

				}
				itemBarchartviewDTO.getDatasets().add(itemBardatasetView);
				context.complete();
				return gson.toJson(itemBarchartviewDTO);
			}
		} catch (SQLException sqlExc) {
			log.error("SQLException occured :", sqlExc);
		} catch (Exception ex) {
			log.error("Exception occured :", ex);
		}
		log.info("Exiting from TrendingMetricsController::getTrendingSearchByDate() method...");
		return null;
	}

	/***
	 * 
	 * @param params
	 * @param response
	 * @param request
	 * @return
	 */
	@GetMapping({ "/getTrendingTypeByDate" })
	@Produces({ "application/json" })
	@Consumes({ "application/json" })
	public String getTrendingTypeByDate(@RequestParam Map<String, String> params, HttpServletResponse response,
			HttpServletRequest request) {
		log.info("Inside TrendingMetricsController::getTrendingTypeByDate() method...");

		int dateType = Integer.valueOf(params.get("dateType"));
		log.info("dateType in request is:", dateType);
		DateEnum dateMonthYear = DateEnum.getDeploymentClass(dateType);
		String documentType = params.get("documentType");
		log.info("documentType in request is:", documentType);

		if (params.get("collectionId") != null) {
			dateMonthYear.setCollectionList(params.get("collectionId"));
		} else {
			dateMonthYear.setCollectionList(Strings.EMPTY);
		}
		try {
			Context context = ContextUtil.obtainContext(request);
			EPerson user = context.getCurrentUser();
			if (context != null && null == user) {
				log.info("user not loggedin..");
			} else {
				log.info("loggedin user:", user);
			}
			if (StringUtils.isNotBlank(documentType)) {

				String searchvalue = Strings.EMPTY;
				if (!documentType.equals("ALL")) {
					searchvalue = "and documenttype ='" + Integer.valueOf(documentType) + "'";
				}
				List<Object[]> iteamViewObjectList = eventTrackService.TypeViewBYDate(context,
						dateMonthYear.getqueryString() + searchvalue);
				ItemBarchartDTO itemBarchartviewDTO = new ItemBarchartDTO();
				ItemBardataset itemBardatasetView = new ItemBardataset("View", "transparent");
				itemBardatasetView.setBorderWidth(3);
				itemBardatasetView.setPointRadius(2);
				itemBardatasetView.setPointBackgroundColor("#1F3F5E");
				itemBardatasetView.setBorderColor("#1F3F5E");
				for (Object[] iteamViewobjects : iteamViewObjectList) {
					SimpleDateFormat DateFor = new SimpleDateFormat("MMM dd yyyy");
					String stringDate = DateFor.format((Date) iteamViewobjects[0]);
					if (!itemBarchartviewDTO.getLabels().contains(stringDate)) {
						itemBarchartviewDTO.getLabels().add(stringDate);
					}
					int count = ((BigInteger) iteamViewobjects[1]).intValue();
					itemBardatasetView.getData().add(count);

				}
				itemBarchartviewDTO.getDatasets().add(itemBardatasetView);
				context.complete();
				return gson.toJson(itemBarchartviewDTO);
			}
		} catch (SQLException sqlExc) {
			log.error("SQLException occured :", sqlExc);
		} catch (Exception ex) {
			log.error("Exception occured :", ex);
		}
		log.info("Exiting from TrendingMetricsController::getTrendingTypeByDate() method...");
		return null;
	}

	/***
	 * 
	 * @param params
	 * @param response
	 * @param request
	 * @return
	 */
	@GetMapping({ "/getcountry" })
	@Produces({ "application/json" })
	@Consumes({ "application/json" })
	public String getcountry(@RequestParam Map<String, String> params, HttpServletResponse response,
			HttpServletRequest request) {
		log.info("Inside TrendingMetricsController::getcountry() method...");

		int dateType = Integer.valueOf(params.get("dateType"));
		log.info("dateType in request is:", dateType);
		DateEnum dateMonthYear = DateEnum.getDeploymentClass(dateType);

		if (params.get("collectionId") != null) {
			dateMonthYear.setCollectionList(params.get("collectionId"));
		} else {
			dateMonthYear.setCollectionList(Strings.EMPTY);
		}
		try {
			Context context = ContextUtil.obtainContext(request);
			EPerson user = context.getCurrentUser();
			if (context != null && null == user) {
				log.info("user not loggedin..");
			} else {
				log.info("loggedin user:", user);
			}
			List<Object[]> countrylist = eventTrackService.getCountByObject(context, dateMonthYear.getqueryString(),
					"");
			Map<String, CountryDTO> countryDTOmap = new LinkedHashMap<>();
			for (Object[] iteamViewobjects : countrylist) {
				String county = iteamViewobjects[0].toString();

				int count = ((BigInteger) iteamViewobjects[1]).intValue();
				int Type = (int) iteamViewobjects[2];
				String countyCode = iteamViewobjects[4].toString();
				CountryDTO countryDTOobj = new CountryDTO(0, 0, 0);

				if (countryDTOmap.containsKey(countyCode)) {
					if (Type == Constants.ITEM) {
						countryDTOmap.get(countyCode).setViews(count);
					}
					if (Type == Constants.BITSTREAM) {
						countryDTOmap.get(countyCode).setDownloads(count);
					}
					if (Type == Constants.SEARCH) {
						countryDTOmap.get(countyCode).setSearches(count);
					}

				} else {
					if (Type == Constants.ITEM) {
						countryDTOobj.setViews(count);
					}
					if (Type == Constants.BITSTREAM) {
						countryDTOobj.setDownloads(count);
					}
					if (Type == Constants.SEARCH) {
						countryDTOobj.setSearches(count);
					}

					List<Object[]> cityList = eventTrackService.getcityByCountry(context,
							dateMonthYear.getqueryString(), county, "");
					Map<String, CountryDTO> cityDTOmap = new LinkedHashMap<>();
					for (Object[] iteamViewobjectsCity : cityList) {
						String city = iteamViewobjectsCity[0].toString();
						int count1 = ((BigInteger) iteamViewobjectsCity[1]).intValue();
						int Type1 = (int) iteamViewobjectsCity[2];
						// String countyCode = iteamViewobjects[4].toString();

						CountryDTO cityDTOobj = new CountryDTO(0, 0, 0);
						if (cityDTOmap.containsKey(city)) {
							if (Type1 == Constants.ITEM) {
								cityDTOmap.get(city).setViews(count1);
							}
							if (Type1 == Constants.BITSTREAM) {
								cityDTOmap.get(city).setDownloads(count1);
							}
							if (Type1 == Constants.SEARCH) {
								cityDTOmap.get(city).setSearches(count1);
							}

						} else {
							if (Type1 == Constants.ITEM) {
								cityDTOobj.setViews(count1);
							}
							if (Type1 == Constants.BITSTREAM) {
								cityDTOobj.setDownloads(count1);
							}
							if (Type1 == Constants.SEARCH) {
								cityDTOobj.setSearches(count1);
							}
							cityDTOobj.setCity(city);
							cityDTOmap.put(city, cityDTOobj);
						}

					}
					log.info("country and cityDTOmap size is : " + county + "" + cityDTOmap.size());

					countryDTOobj.setCity(county);
					countryDTOobj.setCityDTOmap(cityDTOmap);
					countryDTOmap.put(countyCode, countryDTOobj);
				}
			}
			log.info(" countryDTOmap size is : ", countryDTOmap.size());
			Map<String, Map<String, Object>> chartmapparentObjeobject = new LinkedHashMap<>();

			for (Map.Entry<String, CountryDTO> entry : countryDTOmap.entrySet()) {
				Map<String, Object> chartmapchildObjeobject = new LinkedHashMap<>();
				chartmapchildObjeobject.put("value", entry.getValue().getValue());
				// chartmapchildObjeobject.put("tooltip", entry.getValue().getContent());
				chartmapchildObjeobject.put("country", entry.getValue().getCity());
				chartmapchildObjeobject.put("view", entry.getValue().getViews());
				chartmapchildObjeobject.put("download", entry.getValue().getDownloads());
				chartmapchildObjeobject.put("search", entry.getValue().getSearches());
				Map<String, CountryDTO> citiesDTOmap1 = entry.getValue().getCityDTOmap();
				for (Map.Entry<String, CountryDTO> entry1 : citiesDTOmap1.entrySet()) {
					Map<String, Object> ChartmapchildObjeobject1 = new LinkedHashMap<>();
					ChartmapchildObjeobject1.put("valueC", entry1.getValue().getValue());
					// ChartmapchildObjeobject1.put("tooltipC", entry1.getValue().getContent());
					ChartmapchildObjeobject1.put("city", entry1.getValue().getCity());
					ChartmapchildObjeobject1.put("view", entry1.getValue().getViews());
					ChartmapchildObjeobject1.put("download", entry1.getValue().getDownloads());
					ChartmapchildObjeobject1.put("search", entry1.getValue().getSearches());

					// List<Map.Entry<String, Integer> > list = new LinkedList<Map.Entry<String,
					// Integer> >(entry1.entrySet());

					chartmapchildObjeobject.put(entry1.getKey(), ChartmapchildObjeobject1);
				}
				chartmapparentObjeobject.put(entry.getKey(), chartmapchildObjeobject);
			}
			context.complete();
			return gson.toJson(chartmapparentObjeobject);
		} catch (SQLException sqlExc) {
			log.error("SQLException occured :", sqlExc);
		} catch (Exception ex) {
			log.error("Exception occured :", ex);
		}
		log.info("Exiting from TrendingMetricsController::getcountry() method...");
		return null;
	}

	/***
	 * 
	 * @param params
	 * @param response
	 * @param request
	 * @return
	 */
	@GetMapping({ "/getTrendingCommunitieByDate" })
	@Produces({ "application/json" })
	@Consumes({ "application/json" })
	public String getTrendingCommunitieByDate(@RequestParam Map<String, String> params, HttpServletResponse response,
			HttpServletRequest request) {
		log.info("Inside TrendingMetricsController::getTrendingCommunitieByDate() method...");
		ItemBarchartDTO itemBarchartviewDTO = new ItemBarchartDTO();
		int dateType = Integer.valueOf(params.get("dateType"));
		log.info("dateType in request is:", dateType);
		DateEnum dateMonthYear = DateEnum.getDeploymentClass(dateType);
		String communityID = params.get("communityID");
		log.info("communityID in request is:", communityID);

		if (params.get("collectionId") != null) {
			dateMonthYear.setCollectionList(params.get("collectionId"));
		} else {
			dateMonthYear.setCollectionList(Strings.EMPTY);
		}
		try {
			Context context = ContextUtil.obtainContext(request);
			EPerson user = context.getCurrentUser();
			if (context != null && null == user) {
				log.info("user not loggedin..");
			} else {
				log.info("loggedin user:", user);
			}
			if (StringUtils.isNotBlank(communityID)) {

				String query = "";
				if (communityID != null && !communityID.equals("ALL")) {
					UUID uuid = UUID.fromString(communityID);
					Community community = communityService.find(context, uuid);
					query = "and dspaceobjectid ='" + community.getID() + "'";
				}
				List<Object[]> iteamViewObjectList = eventTrackService.CommunitieViewBYDate(context,
						dateMonthYear.getqueryString() + query);

				ItemBardataset itemBardatasetView = new ItemBardataset("View", "transparent");
				itemBardatasetView.setBorderWidth(3);
				itemBardatasetView.setPointRadius(2);
				itemBardatasetView.setPointBackgroundColor("#1F3F5E");
				itemBardatasetView.setBorderColor("#1F3F5E");
				for (Object[] iteamViewobjects : iteamViewObjectList) {
					SimpleDateFormat DateFor = new SimpleDateFormat("MMM dd yyyy");
					String stringDate = DateFor.format((Date) iteamViewobjects[0]);
					if (!itemBarchartviewDTO.getLabels().contains(stringDate)) {
						itemBarchartviewDTO.getLabels().add(stringDate);
					}
					int count = ((BigInteger) iteamViewobjects[1]).intValue();
					itemBardatasetView.getData().add(count);

				}
				itemBarchartviewDTO.getDatasets().add(itemBardatasetView);
			}
			context.complete();
			return gson.toJson(itemBarchartviewDTO);

		} catch (SQLException sqlExc) {
			log.error("SQLException occured :", sqlExc);
		} catch (Exception ex) {
			log.error("Exception occured :", ex);
		}
		log.info("Exiting from TrendingMetricsController::getTrendingCommunitieByDate() method...");
		return null;
	}

	/***
	 * 
	 * @param params
	 * @param response
	 * @param request
	 * @return
	 */
	@GetMapping({ "/getDocumentAllType" })
	@Produces({ "application/json" })
	@Consumes({ "application/json" })
	public String getDocumentAllType(@RequestParam Map<String, String> params, HttpServletResponse response,
			HttpServletRequest request) {
		log.info("Inside TrendingMetricsController::getDocumentAllType() method...");

		List<DocType> docTypes = new ArrayList<>();
		try {
			for (DocumentTypeEnum documentType : DocumentTypeEnum.values()) {
				DocType docType = new DocType(documentType.getId(), documentType.getName());
				docTypes.add(docType);
			}
		} catch (Exception ex) {
			log.error("Exception occured :", ex);
		}
		log.info("Exiting from TrendingMetricsController::getDocumentAllType() method...");
		return gson.toJson(docTypes);
	}

	/***
	 * 
	 * @param params
	 * @param response
	 * @param request
	 * @return
	 */
	@GetMapping({ "/getcountryBySearch" })
	@Produces({ "application/json" })
	@Consumes({ "application/json" })
	public String getcountryBySearch(@RequestParam Map<String, String> params, HttpServletResponse response,
			HttpServletRequest request) {
		log.info("Inside TrendingMetricsController::getcountryBySearch() method...");
		Map<String, CountryDTO> countryDTOmap = new LinkedHashMap<>();
		int dateType = Integer.valueOf(params.get("dateType"));
		log.info("dateType in request is:", dateType);
		DateEnum dateMonthYear = DateEnum.getDeploymentClass(dateType);
		String id = params.get("id");
		log.info("id in request is:", id);
		String typeWise = params.get("typeWise");
		log.info("typeWise in request is:", typeWise);
		String countryName = params.get("countryName");
		log.info("countryName in request is:", countryName);

		if (params.get("collectionId") != null) {
			dateMonthYear.setCollectionList(params.get("collectionId"));
		} else {
			dateMonthYear.setCollectionList(Strings.EMPTY);
		}
		try {
			Context context = ContextUtil.obtainContext(request);
			EPerson user = context.getCurrentUser();
			if (context != null && null == user) {
				log.info("user not loggedin..");
			} else {
				log.info("loggedin user:", user);
			}
			String searchvalue = Strings.EMPTY;
			if (StringUtils.isNotBlank(id)) {
				if (!id.equals("ALL")) {
					searchvalue = "and title ='" + id + "'";
				}
			}
			String quary = Strings.EMPTY;
			if (typeWise.equals("citywise")) {
				quary = "select  ge.city,"
						+ "SUM(CASE WHEN  action =5 and dspaceobjecttype=8  THEN 1 ELSE 0 END)  searchcount"
						+ " from event e left JOIN geolocationevent ge"
						+ " ON e.geolocationeventid = ge.uuid where ge.country='" + countryName
						+ "' and ge.city is not null " + dateMonthYear.getqueryString() + " " + searchvalue
						+ " GROUP by ge.city  order by searchcount desc";
			} else if (typeWise.equals("countrywise")) {
				quary = "select  ge.country,"
						+ "SUM(CASE WHEN  action =5 and dspaceobjecttype=8  THEN 1 ELSE 0 END)  searchcount"
						+ " from event e left JOIN geolocationevent ge"
						+ " ON e.geolocationeventid = ge.uuid where ge.country is not null "
						+ dateMonthYear.getqueryString() + " " + searchvalue
						+ " GROUP by ge.country  order by searchcount desc";
			}

			List<Object[]> countrylist = eventTrackService.ViewCountReport(context, quary);

			for (Object[] iteamViewobjects : countrylist) {
				String county = iteamViewobjects[0].toString();
				int searchcount = ((BigInteger) iteamViewobjects[1]).intValue();
				CountryDTO countryDTOobj = new CountryDTO(0, 0, 0);
				countryDTOobj.setSearches(searchcount);
				countryDTOobj.setCity(county);
				if (searchcount != 0)
					countryDTOmap.put(county, countryDTOobj);
			}
			context.complete();

		} catch (SQLException sqlExc) {
			log.error("SQLException occured :", sqlExc);
		} catch (Exception ex) {
			log.error("Exception occured :", ex);
		}
		log.info("Exiting from TrendingMetricsController::getcountryBySearch() method...");
		return gson.toJson(countryDTOmap);
	}

	/***
	 * 
	 * @param params
	 * @param response
	 * @param request
	 * @return
	 */
	@GetMapping({ "/getItemByTpe" })
	@Produces({ "application/json" })
	@Consumes({ "application/json" })
	public String getItemByTpe(@RequestParam Map<String, String> params, HttpServletResponse response,
			HttpServletRequest request) {
		log.info("Inside TrendingMetricsController::getItemByTpe() method...");

		int dateType = Integer.valueOf(params.get("dateType"));
		log.info("dateType in request is:", dateType);
		DateEnum dateMonthYear = DateEnum.getDeploymentClass(dateType);
		String documentType = params.get("documentType");
		log.info("documentType in request is:", documentType);

		if (params.get("collectionId") != null) {
			dateMonthYear.setCollectionList(params.get("collectionId"));
		} else {
			dateMonthYear.setCollectionList(Strings.EMPTY);
		}
		try {
			Context context = ContextUtil.obtainContext(request);
			EPerson user = context.getCurrentUser();
			if (context != null && null == user) {
				log.info("user not loggedin..");
			} else {
				log.info("loggedin user:", user);
			}
			if (StringUtils.isNotBlank(documentType)) {
				String searchvalue = "";
				if (!documentType.equals("ALL")) {
					searchvalue = "and documenttype ='" + Integer.valueOf(documentType) + "'";
				}
				List<Object[]> iteamViewObjectList = eventTrackService.ItemByType(context,
						dateMonthYear.getqueryString() + searchvalue);

				List<ItemCartDTO> itemCartList = new ArrayList<>();
				for (Object[] objects : iteamViewObjectList) {
					UUID dspaceItemID = UUID.fromString((String) objects[0]);
					ItemCartDTO itemCartDTOobj = new ItemCartDTO();
					Item item = itemService.find(context, dspaceItemID);
					if (item != null) {
						int totalItemView = ((BigInteger) objects[1]).intValue();
						String author = itemService.getMetadataFirstValue(item, "dc", "contributor", "author",
								Item.ANY);
						String Documenttype = itemService.getMetadataFirstValue(item, "dc", "type", null, Item.ANY);
						String title = itemService.getMetadataFirstValue(item, "dc", "title", null, Item.ANY);
						itemCartDTOobj.setTitle(title);
						itemCartDTOobj.setAuther(author);
						itemCartDTOobj.setDocumentType(Documenttype);
						itemCartDTOobj.setTotal_View(totalItemView);
						itemCartDTOobj.setUrl(request.getContextPath() + "/handle/" + item.getHandle());
						itemCartList.add(itemCartDTOobj);
					}
					// List<Object[]> ItemList
					// =eventTrackService.ItemByDate(context,d.getqueryString());
					// itemService.getMetadata(item, "dc.contributor.author")
				}
				context.complete();
				return gson.toJson(itemCartList);
			}
		} catch (SQLException sqlExc) {
			log.error("SQLException occured :", sqlExc);
		} catch (Exception ex) {
			log.error("Exception occured :", ex);
		}
		log.info("Exiting from TrendingMetricsController::getItemByTpe() method...");
		return null;
	}

	/***
	 * 
	 * @param params
	 * @param response
	 * @param request
	 * @return
	 */
	@GetMapping({ "/getcountryBycommunity" })
	@Produces({ "application/json" })
	@Consumes({ "application/json" })
	public String getcountryBycommunity(@RequestParam Map<String, String> params, HttpServletResponse response,
			HttpServletRequest request) {
		log.info("Inside TrendingMetricsController::getcountryBycommunity() method...");
		Map<String, CountryDTO> countryDTOmap = new LinkedHashMap<>();
		int dateType = Integer.valueOf(params.get("dateType"));
		log.info("dateType in request is:", dateType);
		DateEnum dateMonthYear = DateEnum.getDeploymentClass(dateType);
		String id = params.get("id");
		log.info("id in request is:", id);
		String typeWise = params.get("typeWise");
		log.info("typeWise in request is:", typeWise);
		String countryName = params.get("countryName");
		log.info("countryName in request is:", countryName);

		if (params.get("collectionId") != null) {
			dateMonthYear.setCollectionList(params.get("collectionId"));
		} else {
			dateMonthYear.setCollectionList(Strings.EMPTY);
		}
		try {
			Context context = ContextUtil.obtainContext(request);
			EPerson user = context.getCurrentUser();
			if (context != null && null == user) {
				log.info("user not loggedin..");
			} else {
				log.info("loggedin user:", user);
			}
			String searchvalue = Strings.EMPTY;
			if (id != null && !id.equals("ALL")) {
				searchvalue = " and e.dspaceobjectid ='" + id + "'";
			}
			String quary = Strings.EMPTY;
			if (typeWise.equals("citywise")) {
				quary = "select  ge.city,"
						+ "SUM(CASE WHEN  action =4 and dspaceobjecttype=4  THEN 1 ELSE 0 END)  searchcount"
						+ " from event e left JOIN geolocationevent ge"
						+ " ON e.geolocationeventid = ge.uuid where ge.country='" + countryName
						+ "' and ge.city is not null " + dateMonthYear.getqueryString() + " " + searchvalue
						+ " GROUP by ge.city  order by searchcount desc";
			} else if (typeWise.equals("countrywise")) {

				quary = "select  ge.country,"
						+ "SUM(CASE WHEN  action =4 and dspaceobjecttype=4  THEN 1 ELSE 0 END)  searchcount"
						+ " from event e left JOIN geolocationevent ge"
						+ " ON e.geolocationeventid = ge.uuid where ge.country is not null "
						+ dateMonthYear.getqueryString() + " " + searchvalue
						+ " GROUP by ge.country  order by searchcount desc";
			}
			log.info("query is ::" + quary.toString());
			// System.out.println("AnalyticsController.java -- getcountryBySearch
			// "+d.getqueryString() + " " + searchvalue);
			List<Object[]> countrylist = eventTrackService.ViewCountReport(context, quary);

			for (Object[] iteamViewobjects : countrylist) {
				String county = iteamViewobjects[0].toString();
				int searchcount = ((BigInteger) iteamViewobjects[1]).intValue();
				CountryDTO countryDTOobj = new CountryDTO(0, 0, 0);
				countryDTOobj.setSearches(searchcount);
				countryDTOobj.setCity(county);
				if (searchcount != 0)
					countryDTOmap.put(county, countryDTOobj);
			}
			context.complete();
		} catch (SQLException sqlExc) {
			log.error("SQLException occured :", sqlExc);
		} catch (Exception ex) {
			log.error("Exception occured :", ex);
		}
		log.info("Exiting from TrendingMetricsController::getcountryBycommunity() method...");
		return gson.toJson(countryDTOmap);
	}

}