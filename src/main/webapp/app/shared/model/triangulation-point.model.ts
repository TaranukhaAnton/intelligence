import dayjs from 'dayjs';
import { ITriangulationReport } from 'app/shared/model/triangulation-report.model';
import { IFrequency } from 'app/shared/model/frequency.model';

export interface ITriangulationPoint {
  id?: number;
  description?: string | null;
  longitude?: number;
  latitude?: number;
  date?: string;
  triangulationReport?: ITriangulationReport | null;
  frequency?: IFrequency | null;
}

export const defaultValue: Readonly<ITriangulationPoint> = {};
