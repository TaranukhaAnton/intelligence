import dayjs from 'dayjs';
import { ITriangulationPoint } from 'app/shared/model/triangulation-point.model';

export interface ITriangulationReport {
  id?: number;
  date?: string | null;
  name?: string | null;
  conclusion?: string | null;
  points?: ITriangulationPoint[] | null;
}

export const defaultValue: Readonly<ITriangulationReport> = {};
