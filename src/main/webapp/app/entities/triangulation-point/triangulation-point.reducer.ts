import axios from 'axios';
import { createAsyncThunk, isFulfilled, isPending, isRejected } from '@reduxjs/toolkit';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { IQueryParams, createEntitySlice, EntityState, serializeAxiosError } from 'app/shared/reducers/reducer.utils';
import { ITriangulationPoint, defaultValue } from 'app/shared/model/triangulation-point.model';

const initialState: EntityState<ITriangulationPoint> = {
  loading: false,
  errorMessage: null,
  entities: [],
  allEntities: [],
  filteredEntities: [],
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

const apiUrl = 'api/triangulation-points';
const apiUrlAll = 'api/triangulation-points-all';

// Actions

export const getEntities = createAsyncThunk('triangulationPoint/fetch_entity_list', async ({ page, size, sort }: IQueryParams) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}&` : '?'}cacheBuster=${new Date().getTime()}`;
  return axios.get<ITriangulationPoint[]>(requestUrl);
});

export const getAllEntities = createAsyncThunk('triangulationPointAll/fetch_entity_list', async () => {
  const requestUrl = `${apiUrlAll}`;
  return axios.get<ITriangulationPoint[]>(requestUrl);
});

export const getFilteredPoints = createAsyncThunk('triangulationPointAll/fetch_entity_filtered_list', async (id: []) => {
  const requestUrl = `${apiUrlAll}?frequencyId.in=${id}`;
  return axios.get<ITriangulationPoint[]>(requestUrl);
});

export const getEntity = createAsyncThunk(
  'triangulationPoint/fetch_entity',
  async (id: string | number) => {
    const requestUrl = `${apiUrl}/${id}`;
    return axios.get<ITriangulationPoint>(requestUrl);
  },
  { serializeError: serializeAxiosError }
);

export const createEntity = createAsyncThunk(
  'triangulationPoint/create_entity',
  async (entity: ITriangulationPoint, thunkAPI) => {
    const result = await axios.post<ITriangulationPoint>(apiUrl, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const updateEntity = createAsyncThunk(
  'triangulationPoint/update_entity',
  async (entity: ITriangulationPoint, thunkAPI) => {
    const result = await axios.put<ITriangulationPoint>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const partialUpdateEntity = createAsyncThunk(
  'triangulationPoint/partial_update_entity',
  async (entity: ITriangulationPoint, thunkAPI) => {
    const result = await axios.patch<ITriangulationPoint>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const deleteEntity = createAsyncThunk(
  'triangulationPoint/delete_entity',
  async (id: string | number, thunkAPI) => {
    const requestUrl = `${apiUrl}/${id}`;
    const result = await axios.delete<ITriangulationPoint>(requestUrl);
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

// slice

export const TriangulationPointSlice = createEntitySlice({
  name: 'triangulationPoint',
  initialState,
  extraReducers(builder) {
    builder
      .addCase(getEntity.fulfilled, (state, action) => {
        state.loading = false;
        state.entity = action.payload.data;
      })
      .addCase(deleteEntity.fulfilled, state => {
        state.updating = false;
        state.updateSuccess = true;
        state.entity = {};
      })
      .addCase(getAllEntities.fulfilled, (state, action) => {
        state.updating = false;
        state.updateSuccess = true;
        state.allEntities = action.payload.data;
      })
      .addCase(getFilteredPoints.fulfilled, (state, action) => {
        state.updating = false;
        state.updateSuccess = true;
        state.filteredEntities = action.payload.data;
      })
      .addMatcher(isFulfilled(getEntities), (state, action) => {
        const { data, headers } = action.payload;

        return {
          ...state,
          loading: false,
          entities: data,
          totalItems: parseInt(headers['x-total-count'], 10),
        };
      })
      .addMatcher(isFulfilled(createEntity, updateEntity, partialUpdateEntity), (state, action) => {
        state.updating = false;
        state.loading = false;
        state.updateSuccess = true;
        state.entity = action.payload.data;
      })
      .addMatcher(isPending(getEntities, getEntity), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.loading = true;
      })
      .addMatcher(isPending(createEntity, updateEntity, partialUpdateEntity, deleteEntity), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.updating = true;
      });
  },
});

export const { reset } = TriangulationPointSlice.actions;

// Reducer
export default TriangulationPointSlice.reducer;
