import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import TriangulationReport from './triangulation-report';
import TriangulationReportDetail from './triangulation-report-detail';
import TriangulationReportUpdate from './triangulation-report-update';
import TriangulationReportDeleteDialog from './triangulation-report-delete-dialog';

const TriangulationReportRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<TriangulationReport />} />
    <Route path="new" element={<TriangulationReportUpdate />} />
    <Route path=":id">
      <Route index element={<TriangulationReportDetail />} />
      <Route path="edit" element={<TriangulationReportUpdate />} />
      <Route path="delete" element={<TriangulationReportDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TriangulationReportRoutes;
