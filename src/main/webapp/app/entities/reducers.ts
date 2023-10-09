import triangulationPoint from 'app/entities/triangulation-point/triangulation-point.reducer';
import frequency from 'app/entities/frequency/frequency.reducer';
import triangulationReport from 'app/entities/triangulation-report/triangulation-report.reducer';
import geojson from 'app/entities/geojson/geojson.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  triangulationPoint,
  frequency,
  triangulationReport,
  geojson,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
