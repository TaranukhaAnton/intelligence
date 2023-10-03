import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import TriangulationPoint from './triangulation-point';
import TriangulationPointDetail from './triangulation-point-detail';
import TriangulationPointUpdate from './triangulation-point-update';
import TriangulationPointDeleteDialog from './triangulation-point-delete-dialog';

const TriangulationPointRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<TriangulationPoint />} />
    <Route path="new" element={<TriangulationPointUpdate />} />
    <Route path=":id">
      <Route index element={<TriangulationPointDetail />} />
      <Route path="edit" element={<TriangulationPointUpdate />} />
      <Route path="delete" element={<TriangulationPointDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TriangulationPointRoutes;
