import React from 'react';
import { MapContainer, TileLayer, Marker, Popup, MapContainerProps } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import map from 'leaflet';

map.Marker.prototype.options.icon = map.icon({
  iconUrl: 'https://unpkg.com/leaflet@1.7.1/dist/images/marker-icon.png',
});

const zaporizhzhia = { lng: 35.53243155971364, lat: 47.43104300460715 };

export default function LeafletMap() {
  return (
    <MapContainer center={zaporizhzhia} zoom={10.5} style={{ height: '100vh' }}>
      <TileLayer
        attribution="&copy; NASA Blue Marble, image service by OpenGeo"
        url="https://api.maptiler.com/maps/jp-mierune-dark/256/{z}/{x}/{y}@2x.png?key=cdlqpZoyNzLvcRJ0PwMt"
      />
      <Marker position={zaporizhzhia}>
        <Popup>
          <div>Hello!</div>
        </Popup>
      </Marker>
    </MapContainer>
  );
}
