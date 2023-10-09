import axios from 'axios';
import { createAsyncThunk, isFulfilled, isPending, isRejected } from '@reduxjs/toolkit';

import { IQueryParams, IFilterParams, createEntitySlice, EntityState, serializeAxiosError } from 'app/shared/reducers/reducer.utils';
// import { ITriangulationPoint, defaultValue } from 'app/shared/model/triangulation-point.model';
import { IGeoJson, defaultValue } from 'app/shared/model/geojson.model';

const initialState: EntityState<IGeoJson> = {
  geoJson: defaultValue,
  loading: false,
  errorMessage: null,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

const apiUrlGeoJson = 'api/triangulation-points-all-geojson';

// Actions

export const getGeoJson = createAsyncThunk(
  'triangulationPointAllGeojson/fetch_filtered_geojson',
  async ({ id, greaterThanOrEqual, lessThanOrEqual }: IFilterParams) => {
    const requestUrl = `${apiUrlGeoJson}?frequencyId.in=${id}&date.greaterThanOrEqual=${greaterThanOrEqual}&date.lessThanOrEqual=${lessThanOrEqual}`;
    return axios.get<IGeoJson>(requestUrl);
  }
);

// slice

export const GeoJsonSlice = createEntitySlice({
  name: 'geojson',
  initialState,
  extraReducers(builder) {
    builder.addCase(getGeoJson.fulfilled, (state, action) => {
      state.updating = false;
      state.updateSuccess = true;
      state.geoJson = action.payload.data;
    });
  },
});

export const { reset } = GeoJsonSlice.actions;

// Reducer
export default GeoJsonSlice.reducer;
