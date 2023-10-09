import React, { useEffect, useRef, useState } from 'react';
import * as maptilersdk from '@maptiler/sdk';
import '@maptiler/sdk/dist/maptiler-sdk.css';
import './map.css';
import Datepicker from './Datepicker';
import Select from './Select';
import { encodedDate } from 'app/shared/util/date-utils';

// State
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getAllEntities as getAllPoints, getFilteredPoints } from 'app/entities/triangulation-point/triangulation-point.reducer';
import { getAllEntities as getAllfrequencies } from 'app/entities/frequency/frequency.reducer';
import { convertDateTimeFromServer, convertDateTimeFromServer2 } from 'app/shared/util/date-utils';

export default function Map() {
  const dispatch = useAppDispatch();
  const frequenciesAll = useAppSelector(state => state.frequency.allEntities);
  const filteredPoints = useAppSelector(state => state.triangulationPoint.filteredEntities);

  const [frequencyId, setFrequencyId] = useState(null);

  const today = new Date();
  const [greaterThanOrEqualDate, setGreaterThanOrEqualDate] = useState<string | Date | null>(today);
  const [lessThanOrEqualDate, setLessThanOrEqualDate] = useState<string | Date | null>(today);
  const [isFormChecked, setIsFromChecked] = useState<boolean>(false);
  const [isToChecked, setIsToChecked] = useState<boolean>(false);

  const mapContainer = useRef(null);
  const map = useRef(null);
  const zaporizhzhia = { lng: 35.53243155971364, lat: 47.43104300460715 };
  const [zoom] = useState(10.5);
  maptilersdk.config.apiKey = 'QJjHhm7ZqSSIlQdz165Q';
  maptilersdk.config.primaryLanguage = maptilersdk.Language.UKRAINIAN;

  useEffect(() => {
    dispatch(getAllPoints());
    dispatch(getAllfrequencies());
  }, []);

  useEffect(() => {
    if (frequencyId !== null) {
      dispatch(
        getFilteredPoints({
          id: frequencyId,
          greaterThanOrEqual: isFormChecked ? encodedDate(greaterThanOrEqualDate) : '',
          lessThanOrEqual: isToChecked ? encodedDate(lessThanOrEqualDate) : '',
        })
      );
    }
  }, [frequencyId, greaterThanOrEqualDate, lessThanOrEqualDate]);

  useEffect(() => {
    if (map.current) return;

    map.current = new maptilersdk.Map({
      container: mapContainer.current,
      style: maptilersdk.MapStyle.STREETS,
      center: [zaporizhzhia.lng, zaporizhzhia.lat],
      zoom: zoom,
    });

    map.current.on('load', async () => {
      map.current.addSource('places', {
        data: {
          features: [
            {
              geometry: {
                coordinates: [35.5645, 47.4575],
                type: 'Point',
              },
              type: 'Feature',
              properties: {
                description:
                  '<strong>05-10-2023 01:35</strong><p>Ймовірно УКХ р/м управління адн 503 мсп 19 мсд 58А   </p><p>Придурки </p>',
                frequency: 430.11,
              },
            },
            {
              geometry: {
                coordinates: [35.5647, 47.4575],
                type: 'Point',
              },
              type: 'Feature',
              properties: {
                description: '<strong>05-10-2023 02:05</strong><p>Ймовірно УКХ р/м управління адн 503 мсп 19 мсд 58А   </p>',
                frequency: 430.11,
              },
            },
            {
              geometry: {
                coordinates: [35.5647, 47.4575],
                type: 'Point',
              },
              type: 'Feature',
              properties: {
                description: '<strong>05-10-2023 02:06</strong><p>Ймовірно УКХ р/м управління адн 503 мсп 19 мсд 58А   </p>',
                frequency: 430.11,
              },
            },
            {
              geometry: {
                coordinates: [35.5687, 47.4522],
                type: 'Point',
              },
              type: 'Feature',
              properties: {
                description: '<strong>05-10-2023 02:09</strong><p>Ймовірно УКХ р/м управління адн 503 мсп 19 мсд 58А   </p>',
                frequency: 430.11,
              },
            },
            {
              geometry: {
                coordinates: [35.5237, 47.3484],
                type: 'Point',
              },
              type: 'Feature',
              properties: {
                description: '<strong>05-10-2023 09:54</strong><p>Ймовірно УКХ р/м управління адн 503 мсп 19 мсд 58А   </p>',
                frequency: 430.11,
              },
            },
          ],
          type: 'FeatureCollection',
        },
        type: 'geojson',
      });

      map.current.addLayer({
        id: 'places',
        type: 'circle',
        source: 'places',

        paint: {
          'circle-color': '#ff2254',
          'circle-opacity': 0.6,
          'circle-radius': 8,
        },
      });
      map.current.addLayer({
        id: 'places_2',
        type: 'symbol',
        source: 'places',

        layout: {
          'text-field': ['number-format', ['get', 'frequency'], { 'min-fraction-digits': 3, 'max-fraction-digits': 3 }],
          'text-font': ['Open Sans Semibold', 'Arial Unicode MS Bold'],
          'text-size': 10,
          'text-offset': [0, 1.5],
        },
        paint: {
          'text-color': 'black',
        },
      });

      // When a click event occurs on a feature in the places layer, open a popup at the
      // location of the feature, with description HTML from its properties.
      map.current.on('click', 'places', function (e) {
        var coordinates = e.features[0].geometry.coordinates.slice();
        var description = e.features[0].properties.description;

        // Ensure that if the map is zoomed out such that multiple
        // copies of the feature are visible, the popup appears
        // over the copy being pointed to.
        while (Math.abs(e.lngLat.lng - coordinates[0]) > 180) {
          coordinates[0] += e.lngLat.lng > coordinates[0] ? 360 : -360;
        }

        new maptilersdk.Popup().setLngLat(coordinates).setHTML(description).addTo(map.current);
      });
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
        date: convertDateTimeFromServer2(item.date),
        description: item.description,
      };
    });

    if (markersData.length > 0) {
      markersData.forEach(markerData => {
        let html = '<div>' + markerData.date + '<div/>';
        if (markerData.description) {
          html += '<div>' + markerData.description + '<div/>';
        }

        const popup = new maptilersdk.Popup({ offset: 25 }).setHTML(html);
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

  const handleFromDateChange = value => {
    setGreaterThanOrEqualDate(value);
  };

  const handleToDateHandler = value => {
    setLessThanOrEqualDate(value);
  };

  const onSwitchFromHandler = () => {
    setIsFromChecked(prevState => !prevState);
  };
  const onSwitchToHandler = e => {
    console.dir(e.target);
    setIsToChecked(prevState => !prevState);
  };

  return (
    <div className="map-wrap">
      <div className="filters-wrapper">
        <Select data={frequenciesAll} onChange={onChangeFrqHandler} className="frq-select"></Select>
        <Datepicker
          value={greaterThanOrEqualDate}
          onChange={handleFromDateChange}
          onSwitch={onSwitchFromHandler}
          lableText={'Вибрати дату ВІД'}
          isChecked={isFormChecked}
        />
        <Datepicker
          value={lessThanOrEqualDate}
          onChange={handleToDateHandler}
          onSwitch={onSwitchToHandler}
          isChecked={isToChecked}
          lableText={'Вибрати дату ДО'}
        />
      </div>
      <div ref={mapContainer} className="map" />
    </div>
  );
}
