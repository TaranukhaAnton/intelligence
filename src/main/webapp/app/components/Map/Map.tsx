import React, { useRef, useEffect, useState } from 'react';
import * as maptilersdk from '@maptiler/sdk';
import '@maptiler/sdk/dist/maptiler-sdk.css';
import './map.css';

// State
import { useAppSelector, useAppDispatch } from 'app/config/store';
import { getEntities as getPoints } from 'app/entities/triangulation-point/triangulation-point.reducer';

export default function Map() {
  const dispatch = useAppDispatch();
  const triangulationPoints = useAppSelector(state => state.triangulationPoint.entities);

  const mapContainer = useRef(null);
  const map = useRef(null);
  const zaporizhzhia = { lng: 35.1396, lat: 47.8388 };
  const [zoom] = useState(9);
  maptilersdk.config.apiKey = 'QJjHhm7ZqSSIlQdz165Q';

  useEffect(() => {
    dispatch(getPoints({}));
  }, []);

  useEffect(() => {
    if (map.current) return;

    map.current = new maptilersdk.Map({
      container: mapContainer.current,
      style: maptilersdk.MapStyle.STREETS,
      center: [zaporizhzhia.lng, zaporizhzhia.lat],
      zoom: zoom,
    });
  }, [zaporizhzhia.lng, zaporizhzhia.lat, zoom]);

  useEffect(() => {
    // Multiple markers
    const markersData = triangulationPoints?.map(item => {
      return {
        lng: item.longitude,
        lat: item.latitude,
      };
    });

    const markers = [];

    if (markersData.length > 0) {
      markersData.forEach(markerData => {
        const marker = new maptilersdk.Marker({ color: '#FF0000' }).setLngLat(markerData).addTo(map.current);
        markers.push(marker);
      });
    }

    console.log(markersData);

    return () => {
      markers.forEach(marker => {
        marker.remove();
      });
    };
  }, [triangulationPoints]);

  return (
    <div className="map-wrap">
      <div ref={mapContainer} className="map" />
    </div>
  );
}
