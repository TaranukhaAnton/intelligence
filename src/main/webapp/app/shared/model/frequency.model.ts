import { ITriangulationPoint } from 'app/shared/model/triangulation-point.model';

export interface IFrequency {
  id?: number;
  name?: string | null;
  description?: string | null;
  triangulationPoints?: ITriangulationPoint[] | null;
}

export const defaultValue: Readonly<IFrequency> = {};
