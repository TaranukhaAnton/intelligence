export type Coordinates = [number, number];

export type Geometry = {
  coordinates: Coordinates;
  type: 'Point';
};

export interface IFeature {
  geometry: Geometry;
  type: 'Feature';
  properties: {
    description: string;
    frequency: number;
  };
}

export interface IGeoJson {
  data?: {
    features?: IFeature[];
  };
  type?: 'geojson';
}

export const defaultValue: Readonly<IGeoJson> = {};
