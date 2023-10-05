import React, { useRef, useEffect, useState } from 'react';
import * as maptilersdk from '@maptiler/sdk';
import '@maptiler/sdk/dist/maptiler-sdk.css';
import './map.css';

// State
import { useAppSelector, useAppDispatch } from 'app/config/store';
import { getAllEntities as getAllPoints, getFilteredPoints } from 'app/entities/triangulation-point/triangulation-point.reducer';
import { getAllEntities as getAllfrequencies } from 'app/entities/frequency/frequency.reducer';

export default function Map() {
  const dispatch = useAppDispatch();
  const triangulationPointsAll = useAppSelector(state => state.triangulationPoint.allEntities);
  const frequenciesAll = useAppSelector(state => state.frequency.allEntities);
  const filteredPoints = useAppSelector(state => state.triangulationPoint.filteredEntities);

  const [frequencyId, setFrequencyId] = useState(null);

  const mapContainer = useRef(null);
  const map = useRef(null);
  const zaporizhzhia = { lng: 35.53243155971364, lat: 47.43104300460715 };

  const [zoom] = useState(10.5);
  maptilersdk.config.apiKey = 'QJjHhm7ZqSSIlQdz165Q';

  useEffect(() => {
    dispatch(getAllPoints());
    dispatch(getAllfrequencies());
  }, []);

  useEffect(() => {
    if (frequencyId !== null) {
      dispatch(getFilteredPoints(frequencyId));
    }
  }, [frequencyId]);

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
    const markers = [];

    // Multiple markers
    const markersData = filteredPoints?.map(item => {
      return {
        lng: item.longitude,
        lat: item.latitude,
        frequency: item.frequency.name,
      };
    });

    if (markersData.length > 0) {
      markersData.forEach(markerData => {
        const popup = new maptilersdk.Popup({ offset: 25 }).setText(markerData.frequency);
        const marker = new maptilersdk.Marker({ color: '#FF0000' })
          .setLngLat({ lng: markerData.lng, lat: markerData.lat })
          .setPopup(popup)
          .addTo(map.current);
        markers.push(marker);
      });
    }

    return () => {
      markers.forEach(marker => {
        marker.remove();
      });
    };
  }, [filteredPoints]);

  function onChangeFrqHandler(e) {
    var options = e.target.options;
    var value = [];
    for (var i = 0, l = options.length; i < l; i++) {
      if (options[i].selected) {
        value.push(options[i].value);
      }
    }
    // Set optionId heare
    setFrequencyId(value);
  }

  return (
    <div className="map-wrap">
      <select name="frq-select[]" onChange={onChangeFrqHandler} multiple={true}>
        <option value="">Виберіть частоту</option>
        {frequenciesAll.map(frequency => {
          return (
            <option key={frequency.id} value={frequency.id}>
              {frequency.name}
            </option>
          );
        })}
      </select>
      <div ref={mapContainer} className="map" />
    </div>
  );
}
