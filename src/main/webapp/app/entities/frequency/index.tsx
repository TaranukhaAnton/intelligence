import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Frequency from './frequency';
import FrequencyDetail from './frequency-detail';
import FrequencyUpdate from './frequency-update';
import FrequencyDeleteDialog from './frequency-delete-dialog';

const FrequencyRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Frequency />} />
    <Route path="new" element={<FrequencyUpdate />} />
    <Route path=":id">
      <Route index element={<FrequencyDetail />} />
      <Route path="edit" element={<FrequencyUpdate />} />
      <Route path="delete" element={<FrequencyDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default FrequencyRoutes;
