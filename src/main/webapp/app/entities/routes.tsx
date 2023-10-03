import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import TriangulationPoint from './triangulation-point';
import Frequency from './frequency';
import TriangulationReport from './triangulation-report';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="triangulation-point/*" element={<TriangulationPoint />} />
        <Route path="frequency/*" element={<Frequency />} />
        <Route path="triangulation-report/*" element={<TriangulationReport />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
