/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.dspace.app.rest.enums.DateEnum;
import org.dspace.app.rest.model.LineChartDTO;
import org.dspace.app.rest.utils.ContextUtil;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.service.EventTrackService;
import org.dspace.content.service.ItemService;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.core.Utils;
import org.dspace.eperson.EPerson;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.dspace.app.rest.enums.DocumentTypeEnum;
import org.dspace.app.rest.model.BarChartDTO;
import org.dspace.app.rest.model.BarChartdataPointsDTO;
import org.dspace.app.rest.model.ItemBarchartDTO;
import org.dspace.app.rest.model.ItemBardataset;
import org.dspace.app.rest.model.ItemCartDTO;
import org.dspace.app.rest.model.LineChartdataPointsDTO;
import com.google.gson.Gson;

@RestController
@RequestMapping({ "/api/trendingItem" })
public class TrendingItemsController implements InitializingBean {
	@Autowired
	private DiscoverableEndpointsService discoverableEndpointsService;

	private static Logger log = LogManager.getLogger(TrendingItemsController.class);

	@Autowired
	EventTrackService eventTrackService;

	@Autowired
	ItemService itemService;

	Gson gson = new Gson();

	public void afterPropertiesSet() throws Exception {
		this.discoverableEndpointsService.register(this,
				Arrays.asList(new Link[] { Link.of("/api/trendingItem", "trendingItem") }));
	}

	/***
	 * 
	 * @param params
	 * @param response
	 * @param request
	 * @return
	 */
	@GetMapping({ "/getItemBarChart" })
	@Produces({ "application/json" })
	@Consumes({ "application/json" })
	public String getItemBarChart(@RequestParam Map<String, String> params, HttpServletResponse response,
			HttpServletRequest request) {
		log.info("Inside TrendingItemsController::getItemBarChart() method...");

		List<BarChartDTO> itemBarChartList = new ArrayList<>();
		try {
			Context context = ContextUtil.obtainContext(request);
			EPerson user = context.getCurrentUser();

			if (context != null && null == user) {
				log.info("user not loggedin..");
			} else {
				log.info("loggedin user:", user);
			}

			DateEnum dateMonthYear = DateEnum.getDeploymentClass(Integer.parseInt(params.get("dateType")));

			if (params.get("collectionId") != null) {
				dateMonthYear.setCollectionList(((String) params.get("collectionId")).toString());
			} else {
				dateMonthYear.setCollectionList(Strings.EMPTY);
			}
			Map<String, Map<String, Integer>> sapprationDataMap = new HashMap<String, Map<String, Integer>>();
			List<Object[]> ItemList = eventTrackService.itemBarChart(context, dateMonthYear.getqueryString(),
					"e.action");
			List<BarChartdataPointsDTO> blankList = new ArrayList<BarChartdataPointsDTO>();
			BarChartDTO view = new BarChartDTO("bar", "Views", "#0.##", blankList, "#014D65");
			BarChartDTO download = new BarChartDTO("bar", "Attachment Views", "#0.##", blankList, "#92D050");

			for (Object[] objects : ItemList) {
				if (objects[0] != null && objects[0].toString().length() != 0) {
					if (!sapprationDataMap.containsKey(objects[0].toString())) {
						Map<String, Integer> downlodemap = new HashMap();
						downlodemap.put("downlode", 0);
						downlodemap.put("view", 0);
						sapprationDataMap.put(objects[0].toString(), downlodemap);
					}
				}
			}
			for (Object[] objects : ItemList) {
				if (objects[0] != null && objects[0].toString().length() != 0) {
					int action = (int) objects[3];
					int count = ((BigInteger) objects[2]).intValue();
					if (action == Constants.ITEM) {
						sapprationDataMap.get(objects[0].toString()).put("view", count);
					} else if (action == Constants.BITSTREAM) {
						sapprationDataMap.get(objects[0].toString()).put("downlode", count);
					}
				}
			}
			for (Map.Entry<String, Map<String, Integer>> entry : sapprationDataMap.entrySet()) {
				Object key = entry.getKey();
				for (Map.Entry<String, Integer> innnerview : entry.getValue().entrySet()) {
					String Type = innnerview.getKey();
					if (Type.equals("view")) {
						view.getDataPoints().add(new BarChartdataPointsDTO(key.toString(), innnerview.getValue()));
					}
					if (Type.equals("downlode")) {
						download.getDataPoints().add(new BarChartdataPointsDTO(key.toString(), innnerview.getValue()));
					}
				}
			}
			itemBarChartList.add(view);
			itemBarChartList.add(download);
			context.complete();
		} catch (SQLException sqlExc) {
			log.error("SQLException occured :", sqlExc);
		} catch (Exception ex) {
			log.error("Exception occured :", ex);
		}
		log.info("Exiting from TrendingItemsController::getItemBarChart() method...");
		return gson.toJson(itemBarChartList);
	}

	/***
	 * 
	 * @param params
	 * @param response
	 * @param request
	 * @return
	 */
	@GetMapping({ "/getItemLineChart" })
	@Produces({ "application/json" })
	@Consumes({ "application/json" })
	public String getItemLineChart(@RequestParam Map<String, String> params, HttpServletResponse response,
			HttpServletRequest request) {
		log.info("Inside TrendingItemsController::getItemLineChart() method...");
		int dateType = Integer.parseInt(params.get("dateType"));
		DateEnum dateOrMonthOrYear = DateEnum.getDeploymentClass(dateType);

		if (params.get("collectionId") != null) {
			dateOrMonthOrYear.setCollectionList(params.get("collectionId").toString());
		} else {
			dateOrMonthOrYear.setCollectionList(Strings.EMPTY);
		}
		List<LineChartDTO> itemLineChartList = new ArrayList<>();
		Context context = ContextUtil.obtainContext(request);
		EPerson user = context.getCurrentUser();

		if (context != null && null == user) {
			log.info("user not loggedin..");
		} else {
			log.info("loggedin user:", user);
		}
		try {
			Map<String, LineChartDTO> docTypeMap = new HashMap<>();
			List<Object[]> ItemList = eventTrackService.itemLineChart(context, dateOrMonthOrYear.getqueryString(),
					"CAST(e.action_date AS DATE)");
			for (DocumentTypeEnum t : DocumentTypeEnum.values()) {
				docTypeMap.put(t.getName(), new LineChartDTO("spline", t.getName(), "#0.##",
						new ArrayList<LineChartdataPointsDTO>(), t.getColour()));
			}
			for (Object[] objects : ItemList) {
				int count = ((BigInteger) objects[2]).intValue();
				int type = ((Integer) objects[1]).intValue();
				String date = ((Date) objects[0]).toString();
				DocumentTypeEnum documentTypeEnumobj = DocumentTypeEnum.getBykey(type);
				LineChartdataPointsDTO bookDataDTO = new LineChartdataPointsDTO(date, count);
				docTypeMap.get(documentTypeEnumobj.getName()).getDataPoints().add(bookDataDTO);
			}

			for (Map.Entry<String, LineChartDTO> entry : docTypeMap.entrySet()) {
				if (entry.getValue().getDataPoints().size() != 0) {
					itemLineChartList.add(entry.getValue());
				}
			}
			context.complete();
		} catch (SQLException sqlExc) {
			log.error("SQLException occured :", sqlExc);
		} catch (Exception ex) {
			log.error("Exception occured :", ex);
		}
		log.info("Exiting from TrendingItemsController::getItemLineChart() method...");
		return gson.toJson(itemLineChartList);
	}

	/***
	 * 
	 * @param params
	 * @param response
	 * @param request
	 * @return
	 */
	@GetMapping({ "/itemCart" })
	@Produces({ "application/json" })
	@Consumes({ "application/json" })
	public String itemCart(@RequestParam Map<String, String> params, HttpServletResponse response,
			HttpServletRequest request) {
		log.info("Inside TrendingItemsController::itemCart() method...");
		List<ItemCartDTO> itemCartList = new ArrayList<ItemCartDTO>();

		int dateType = Integer.valueOf(params.get("dateType"));
		log.info("dateType in request is:", dateType);
		DateEnum dateMonthYear = DateEnum.getDeploymentClass(dateType);
		String collectionId = params.get("collectionId");
		log.info("collectionId in request is:", collectionId);
		try {

			if (collectionId != null) {
				dateMonthYear.setCollectionList(collectionId);
			} else {
				dateMonthYear.setCollectionList(Strings.EMPTY);
			}
			Context context = ContextUtil.obtainContext(request);
			EPerson user = context.getCurrentUser();

			if (context != null && null == user) {
				log.info("user not loggedin..");
			} else {
				log.info("loggedin user:", user);
			}
			List<Object[]> itemMostvList = eventTrackService.ItemByDate(context, dateMonthYear.getqueryString());
			for (Object[] objects : itemMostvList) {
				UUID dspaceItemID = UUID.fromString((String) objects[0]);
				ItemCartDTO ItemCartDTOobj = new ItemCartDTO();
				Item item = itemService.find(context, dspaceItemID);
				if (item != null) {
					ItemCartDTOobj = getItemDTO(request, item, context, dateMonthYear);
					itemCartList.add(ItemCartDTOobj);
				}

			}
			context.complete();
		} catch (SQLException sqlExc) {
			log.error("SQLException occured :", sqlExc);
		} catch (Exception ex) {
			log.error("Exception occured :", ex);
		}
		log.info("Exiting from TrendingItemsController::itemCart() method...");
		return gson.toJson(itemCartList);
	}

	/***
	 * 
	 * @param params
	 * @param response
	 * @param request
	 * @return
	 * @throws SQLException
	 * @throws AuthorizeException
	 */
	@GetMapping({ "/renderitemCartChart" })
	@Produces({ "application/json" })
	@Consumes({ "application/json" })
	public String renderitemCartChart(@RequestParam Map<String, String> params, HttpServletResponse response,
			HttpServletRequest request) throws SQLException, AuthorizeException {
		log.info("Inside TrendingItemsController::renderitemCartChart() method...");

		ItemCartDTO itemCartDTO = new ItemCartDTO();
		Context context = ContextUtil.obtainContext(request);
		// ContextUtil.obtainContext(request);
		EPerson user = context.getCurrentUser();
		try {
			if (context != null && null == user) {
				log.info("user not loggedin..");
			} else {
				log.info("loggedin user:", user);
			}

			int dateType = Integer.valueOf(params.get("dateType"));
			log.info("dateType in request is:", dateType);
			DateEnum dateMonthYear = DateEnum.getDeploymentClass(dateType);
			String collectionId = params.get("collectionId");
			log.info("collectionId in request is:", collectionId);
			String uid = params.get("id");
			log.info("uid in request is :", uid);
			UUID dspaceItemID = UUID.fromString(uid);

			if (collectionId != null) {
				dateMonthYear.setCollectionList(collectionId);
			} else {
				dateMonthYear.setCollectionList(Strings.EMPTY);
			}

			Item item = itemService.find(context, dspaceItemID);
			if (item != null) {

				DateEnum alltime = DateEnum.getDeploymentClass(3);
				alltime.setCollectionList(dateMonthYear.getCollectionList());
				itemCartDTO = getItemcartDTO(request, item, context, alltime, dateMonthYear);
				itemCartDTO.setUuid(uid);
				List<Object[]> iteamViewObjectList = eventTrackService.ItemViewBYDate(context,
						dateMonthYear.getqueryString() + "and dspaceobjectid ='" + item.getID() + "'");
				ItemBarchartDTO ItemBarchartviewDTO = new ItemBarchartDTO();
				ItemBardataset ItemBardatasetView = new ItemBardataset("View", "transparent");
				ItemBardatasetView.setBorderWidth(3);
				ItemBardatasetView.setPointRadius(2);
				ItemBardatasetView.setPointBackgroundColor("#1F3F5E");
				ItemBardatasetView.setBorderColor("#1F3F5E");
				for (Object[] iteamViewobjects : iteamViewObjectList) {
					SimpleDateFormat DateFor = new SimpleDateFormat("MMM dd yyyy");
					String stringDate = DateFor.format((Date) iteamViewobjects[0]);
					ItemBarchartviewDTO.getLabels().add(stringDate);
					int count = ((BigInteger) iteamViewobjects[1]).intValue();
					ItemBardatasetView.getData().add(count);

				}
				ItemBarchartviewDTO.getDatasets().add(ItemBardatasetView);
				List<Object[]> iteamDownlodeObjectList = eventTrackService.ItemDownlodeBYDate(context,
						dateMonthYear.getqueryString() + "and dspaceobjectid ='" + item.getID() + "'");
				ItemBarchartDTO ItemBarchartDownlodeDTO = new ItemBarchartDTO();
				ItemBardataset ItemBardatasetDownlode = new ItemBardataset("View", "transparent");
				ItemBardatasetDownlode.setBorderWidth(3);
				ItemBardatasetDownlode.setPointRadius(2);
				ItemBardatasetDownlode.setPointBackgroundColor("#92D050");
				ItemBardatasetDownlode.setBorderColor("#92D050");
				for (Object[] iteamDownlodeobjects : iteamDownlodeObjectList) {
					SimpleDateFormat DateFor = new SimpleDateFormat("MMM dd yyyy");
					String stringDate = DateFor.format((Date) iteamDownlodeobjects[0]);
					if (!ItemBarchartDownlodeDTO.getLabels().contains(stringDate)) {
						ItemBarchartDownlodeDTO.getLabels().add(stringDate);
					}
					int count = ((BigInteger) iteamDownlodeobjects[1]).intValue();
					ItemBardatasetDownlode.getData().add(count);

				}
				ItemBarchartDownlodeDTO.getDatasets().add(ItemBardatasetDownlode);
				itemCartDTO.setViewDatapoint(ItemBarchartviewDTO);
				itemCartDTO.setDownlodeDatapoint(ItemBarchartDownlodeDTO);
			}
			context.commit();
			context.complete();
		} catch (SQLException sqlExc) {
			log.error("SQLException occured :", sqlExc);
		} catch (Exception ex) {
			log.error("Exception occured :", ex);
		}
		log.info("Exiting from TrendingItemsController::renderitemCartChart() method...");
		return gson.toJson(itemCartDTO);
	}

	/***
	 * 
	 * @param request
	 * @param item
	 * @param context
	 * @param alltime
	 * @param d
	 * @return
	 * @throws Exception
	 */
	public ItemCartDTO getItemcartDTO(HttpServletRequest request, Item item, Context context, DateEnum alltime,
			DateEnum d) throws Exception {
		ItemCartDTO ItemCartDTOobj = new ItemCartDTO();
		int daysinterval = alltime.getId();
		alltime.setId(3);
		int totalItemView = eventTrackService.totalItemView(context,
				alltime.getqueryString() + "and dspaceobjectid ='" + item.getID() + "'");
		int totalItemDownlode = eventTrackService.totalItemDownlode(context,
				alltime.getqueryString() + "and dspaceobjectid ='" + item.getID() + "'");
		alltime.setId(daysinterval);
		int totalItemReader = eventTrackService.totalItemDownlode(context,
				alltime.getqueryString() + "and dspaceobjectid ='" + item.getID() + "'");
		int totalViewByItem = eventTrackService.totalItemView(context,
				d.getqueryString() + " and dspaceobjectid ='" + item.getID() + "'");
		int totalDownlodeByItem = eventTrackService.totalItemDownlode(context,
				d.getqueryString() + "and dspaceobjectid ='" + item.getID() + "'");
		int totalReaderBYItem = eventTrackService.totalItemDownlode(context,
				d.getqueryString() + "and dspaceobjectid ='" + item.getID() + "'");
		ItemCartDTOobj.setTotal_View(totalItemView);
		ItemCartDTOobj.setTotal_View_item(totalViewByItem);
		ItemCartDTOobj.setTotal_Downlode(totalItemDownlode);
		ItemCartDTOobj.setTotal_Downlode_Item(totalDownlodeByItem);
		ItemCartDTOobj.setTotal_Reader(totalItemReader);
		ItemCartDTOobj.setTotal_Reader_item(totalReaderBYItem);
		return ItemCartDTOobj;
	}

	/***
	 * 
	 * @param request
	 * @param item
	 * @param context
	 * @param d
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * @throws SQLException
	 */
	public ItemCartDTO getItemDTO(HttpServletRequest request, Item item, Context context, DateEnum d)
			throws ServletException, IOException, SQLException {
		ItemCartDTO ItemCartDTOobj = new ItemCartDTO();
		List<MetadataValue> MetadataValueauthor = itemService.getMetadata(item, "dc", "contributor", "author",
				Item.ANY);
		String author = "<div class=\"widthit displayAuthor\">";
		int count = 0;
		int cntEnd = MetadataValueauthor.size();
		if (MetadataValueauthor != null) {
			for (MetadataValue m : MetadataValueauthor) {
				if (m != null && m.getValue() != null) {
					if (count == 0 || count == 1) {
						author = author + "<div class=\"displayAuthorContent \">";
						author = author + " <div class=\"col-sm-2 imgdiv\"><i class=\"fa fa-user\"></i></div>";
						author = author + " <div class=\"valueit col-sm-10\" title=" + m.getValue() + "id=\"viewId\">";
						author = author + " <a style=\"white-space: nowrap; color:#000\" href="
								+ request.getContextPath()
								+ "/simple-search?query=&sort_by=score&order=desc&rpp=10&etal=0&filtername=author&filterquery="
								+ m.getValue() + "&filtertype=equals\" title=" + m.getValue() + " >";
						author = author + Utils.addEntities(StringUtils.abbreviate(m.getValue(), 40));
						author = author + " </a>";
						author = author + "</div> ";
						author = author + "</div> ";
						count++;
					} else {
						if (count == 2) {
							author = author + "<div class=\"author_dropdown\">";
							author = author
									+ "<div id=\"seeauthlist\" class=\"dropbtn\" data-toggle=\"dropdown\"> </div>";
							author = author
									+ "<div aria-labelledby=\"seeauthlist\" id=\"authDropdown\" class=\"authdropdown_body dropdown-menu\">";
							author = author + " <div  class=\"authdropdown-content\">";
						}
						author = author + "<a style=\"white-space: nowrap;\" href=" + request.getContextPath()
								+ "/simple-search?query=&sort_by=score&order=desc&rpp=10&etal=0&filtername=author&filterquery="
								+ m.getValue() + "&filtertype=equals\" title=" + m.getValue() + " >"
								+ Utils.addEntities(StringUtils.abbreviate(m.getValue(), 40)) + "</a>";
						count++;
						if (count == cntEnd) {
							author = author + "</div> ";
							author = author + "</div> ";
							author = author + "</div> ";
						}
					}
				}
				// author=author+"<a style='color:#000' class='fauser' href=''
				// title=''>"+m.getValue()+"</a>&nbsp&nbsp";
			}
		}
		author = author + "</div>";
		String Documenttype = itemService.getMetadataFirstValue(item, "dc", "type", null, Item.ANY);
		String title = itemService.getMetadataFirstValue(item, "dc", "title", null, Item.ANY);
		ItemCartDTOobj.setTitle(title);
		ItemCartDTOobj.setAuther(author);
		ItemCartDTOobj.setDocumentType(Documenttype);
		ItemCartDTOobj.setUuid(item.getID().toString());
		ItemCartDTOobj.setUrl(request.getContextPath() + "/handle/" + item.getHandle());
		return ItemCartDTOobj;
	}

}
