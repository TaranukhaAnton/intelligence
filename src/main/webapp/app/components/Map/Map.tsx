import React, { useEffect, useRef, useState } from 'react';
import * as maptilersdk from '@maptiler/sdk';
import '@maptiler/sdk/dist/maptiler-sdk.css';
import './map.css';
import Datepicker from './Datepicker';
import Select from './Select';
import { encodedDate } from 'app/shared/util/date-utils';

// State
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getGeoJson } from 'app/entities/geojson/geojson.reducer';
import { getAllEntities as getAllfrequencies } from 'app/entities/frequency/frequency.reducer';
import { IGeoJson } from 'app/shared/model/geojson.model';

export default function Map() {
  const dispatch = useAppDispatch();

  const frequenciesAll = useAppSelector(state => state.frequency.allEntities);
  const geoJson = useAppSelector<IGeoJson>(state => state.geojson.geoJson);

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
    dispatch(getAllfrequencies());
  }, []);

  useEffect(() => {
    dispatch(
      getGeoJson({
        id: frequencyId || '',
        greaterThanOrEqual: isFormChecked ? encodedDate(greaterThanOrEqualDate) : '',
        lessThanOrEqual: isToChecked ? encodedDate(lessThanOrEqualDate) : '',
      })
    );
  }, [frequencyId, lessThanOrEqualDate, greaterThanOrEqualDate]);

  function initializeMap() {
    if (map.current) return;

    map.current = new maptilersdk.Map({
      container: mapContainer.current,
      style: maptilersdk.MapStyle.STREETS,
      center: [zaporizhzhia.lng, zaporizhzhia.lat],
      zoom: zoom,
    });
  }

  useEffect(() => {
    initializeMap();
  }, [zaporizhzhia.lng, zaporizhzhia.lat, zoom]);

  useEffect(() => {
    if (!map.current || !geoJson) return;

    // Check if the 'places' source exists
    if (map.current.getSource('places')) {
      map.current.getSource('places').setData(geoJson.data); // Update GeoJSON data
    } else {
      map.current.on('load', async () => {
        map.current.addSource('places', {
          type: geoJson.type,
          data: geoJson.data,
        });

        // adding points layer
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
        // adding coordinates near point
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

          // Ensure that if the map is zoomed out such that multiple3012
          // copies of the feature are visible, the popup appears
          // over the copy being pointed to.
          while (Math.abs(e.lngLat.lng - coordinates[0]) > 180) {
            coordinates[0] += e.lngLat.lng > coordinates[0] ? 360 : -360;
          }

          new maptilersdk.Popup().setLngLat(coordinates).setHTML(description).addTo(map.current);
        });
      });
    }
  }, [geoJson, frequencyId]);

  function onChangeFrqHandler(e) {
    var options = e.target.options;
    var value = [];
    for (var i = 0, l = options.length; i < l; i++) {
      if (options[i].selected) {
        value.push(options[i].value);
      }
    } // Set optionId heare
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
          labelText={'Вибрати дату ВІД'}
          isChecked={isFormChecked}
        />
        <Datepicker
          value={lessThanOrEqualDate}
          onChange={handleToDateHandler}
          onSwitch={onSwitchToHandler}
          isChecked={isToChecked}
          labelText={'Вибрати дату ДО'}
        />
      </div>
      <div ref={mapContainer} className="map" />
    </div>
  );
}
