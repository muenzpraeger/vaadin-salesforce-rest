package com.winkelmeyer.salesforce.vaadin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.GeoJsonObject;
import org.geojson.LngLatAlt;
import org.geojson.MultiPolygon;
import org.geojson.Polygon;
import org.vaadin.addon.leaflet.AbstractLeafletVector;
import org.vaadin.addon.leaflet.LLayerGroup;
import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LOpenStreetMapLayer;
import org.vaadin.addon.leaflet.LPolygon;
import org.vaadin.addon.leaflet.LeafletLayer;
import org.vaadin.addon.leaflet.shared.Point;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;

public class ChoroplethMap extends LMap {
	
	private Map<String, LeafletLayer> stateToLayer = new HashMap<>();;

	public ChoroplethMap() {
		setSizeFull();
		addLayer(new LOpenStreetMapLayer());
		readUSStatesFromGeoJson();
		zoomToContent();
	}

	private void readUSStatesFromGeoJson() {
		/*
		 * Reading from geojson here, but typically you'd just query your DB
		 * directly for the data.
		 */
		try {
			FeatureCollection states = new ObjectMapper().readValue(getClass().getResource("/us-states.json"),
					FeatureCollection.class);
			for (Feature f : states.getFeatures()) {
				GeoJsonObject g = f.getGeometry();
				String name = f.getProperty("name");
				if (g instanceof Polygon) {
					Polygon p = (Polygon) g;

					Point[] points = p.getExteriorRing().stream()
							.map(lla -> new Point(lla.getLatitude(), lla.getLongitude()))
							.collect(Collectors.toList())
							.toArray(new Point[0]);
					LPolygon layer = new LPolygon(points);
					stateToLayer.put(name, layer);
					initLayer(layer);
					addLayer(layer);
				} else if (g instanceof MultiPolygon) {
					MultiPolygon p = (MultiPolygon) g;
					List<List<List<LngLatAlt>>> coordinates = p.getCoordinates();
		            LLayerGroup group = new LLayerGroup();
					stateToLayer.put(name, group);
		            for (List<List<LngLatAlt>> pg : coordinates) {
		            	List<LngLatAlt> list = pg.get(0);
						Point[] points = list.stream()
								.map(lla -> new Point(lla.getLatitude(), lla.getLongitude()))
								.collect(Collectors.toList())
								.toArray(new Point[0]);
						LPolygon layer = new LPolygon(points);
						initLayer(layer);
						group.addComponent(layer);
		            }
		            addLayer(group);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
    private void initLayer(LPolygon lPolygon) {
		lPolygon.setFillColor(getColor(0));
        lPolygon.setColor("white");
        lPolygon.setDashArray("3");
        lPolygon.setWeight(2);
        lPolygon.setFillOpacity(0.7);
        lPolygon.setLineCap("");
	}

	protected void configureFeature(LeafletLayer l, final Double density, final String name) {
    	if (l instanceof AbstractLeafletVector) {
			AbstractLeafletVector lv = (AbstractLeafletVector) l;
			configureVector(density, name, lv);
		} else if (l instanceof LLayerGroup) {
			LLayerGroup lg = (LLayerGroup) l;
			for (Component component : lg) {
				configureFeature((LeafletLayer) component, density, name);
			}
		}
    }

	private void configureVector(final Double density, final String name, AbstractLeafletVector lPolygon) {
		lPolygon.setFillColor(getColor(density));
        lPolygon.addClickListener(e -> Notification.show("There are " + density + " lead(s) in " + name));
	}
    
    private String getColor(double d) {
        return d > 8 ? "#800026"
                : d > 6 ? "#BD0026"
                        : d > 5 ? "#E31A1C"
                                : d > 4 ? "#FC4E2A"
                                        : d > 3 ? "#FD8D3C"
                                                : d > 2 ? "#FEB24C"
                                                        : d > 1 ? "#FED976"
                                                                : "#FFEDA0";
    }

	public void setValue(String key, Long value) {
		String name = abbrToName.get(key);
		LeafletLayer leafletLayer = stateToLayer.get(name);
		if(leafletLayer != null) {
			configureFeature(leafletLayer, value.doubleValue(), name);
		}
	}
	
	Map<String,String> abbrToName = new HashMap<>(); 
	{
		abbrToName.put("Alabama", "AL");
		abbrToName.put("Alaska", "AK");
		abbrToName.put("Arizona", "AZ");
		abbrToName.put("Arkansas", "AR");
		abbrToName.put("California", "CA");
		abbrToName.put("Colorado", "CO");
		abbrToName.put("Connecticut", "CT");
		abbrToName.put("Delaware", "DE");
		abbrToName.put("Florida", "FL");
		abbrToName.put("Georgia", "GA");
		abbrToName.put("Hawaii", "HI");
		abbrToName.put("Idaho", "ID");
		abbrToName.put("Illinois", "IL");
		abbrToName.put("Indiana", "IN");
		abbrToName.put("Iowa", "IA");
		abbrToName.put("Kansas", "KS");
		abbrToName.put("Kentucky", "KY");
		abbrToName.put("Louisiana", "LA");
		abbrToName.put("Maine", "ME");
		abbrToName.put("Maryland", "MD");
		abbrToName.put("Massachusetts", "MA");
		abbrToName.put("Michigan", "MI");
		abbrToName.put("Minnesota", "MN");
		abbrToName.put("Mississippi", "MS");
		abbrToName.put("Missouri", "MO");
		abbrToName.put("Montana", "MT");
		abbrToName.put("Nebraska", "NE");
		abbrToName.put("Nevada", "NV");
		abbrToName.put("New Hampshire", "NH");
		abbrToName.put("New Jersey", "NJ");
		abbrToName.put("New Mexico", "NM");
		abbrToName.put("New York", "NY");
		abbrToName.put("North Carolina", "NC");
		abbrToName.put("North Dakota", "ND");
		abbrToName.put("Ohio", "OH");
		abbrToName.put("Oklahoma", "OK");
		abbrToName.put("Oregon", "OR");
		abbrToName.put("Pennsylvania", "PA");
		abbrToName.put("Rhode Island", "RI");
		abbrToName.put("South Carolina", "SC");
		abbrToName.put("South Dakota", "SD");
		abbrToName.put("Tennessee", "TN");
		abbrToName.put("Texas", "TX");
		abbrToName.put("Utah", "UT");
		abbrToName.put("Vermont", "VT");
		abbrToName.put("Virginia", "VA");
		abbrToName.put("Washington", "WA");
		abbrToName.put("West Virginia", "WV");
		abbrToName.put("Wisconsin", "WI");
		abbrToName.put("Wyoming", " WY");
		Object[] array = abbrToName.keySet().toArray();
		for (Object key : array) {
			abbrToName.put(abbrToName.get(key), (String) key);
		}
	}


}
