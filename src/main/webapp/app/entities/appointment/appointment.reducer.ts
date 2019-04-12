import axios from 'axios';
import { ICrudSearchAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IAppointment, defaultValue } from 'app/shared/model/appointment.model';

export const ACTION_TYPES = {
  SEARCH_APPOINTMENTS: 'appointment/SEARCH_APPOINTMENTS',
  FETCH_APPOINTMENT_LIST: 'appointment/FETCH_APPOINTMENT_LIST',
  FETCH_APPOINTMENT: 'appointment/FETCH_APPOINTMENT',
  CREATE_APPOINTMENT: 'appointment/CREATE_APPOINTMENT',
  UPDATE_APPOINTMENT: 'appointment/UPDATE_APPOINTMENT',
  DELETE_APPOINTMENT: 'appointment/DELETE_APPOINTMENT',
  RESET: 'appointment/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IAppointment>,
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type AppointmentState = Readonly<typeof initialState>;

// Reducer

export default (state: AppointmentState = initialState, action): AppointmentState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_APPOINTMENTS):
    case REQUEST(ACTION_TYPES.FETCH_APPOINTMENT_LIST):
    case REQUEST(ACTION_TYPES.FETCH_APPOINTMENT):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_APPOINTMENT):
    case REQUEST(ACTION_TYPES.UPDATE_APPOINTMENT):
    case REQUEST(ACTION_TYPES.DELETE_APPOINTMENT):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_APPOINTMENTS):
    case FAILURE(ACTION_TYPES.FETCH_APPOINTMENT_LIST):
    case FAILURE(ACTION_TYPES.FETCH_APPOINTMENT):
    case FAILURE(ACTION_TYPES.CREATE_APPOINTMENT):
    case FAILURE(ACTION_TYPES.UPDATE_APPOINTMENT):
    case FAILURE(ACTION_TYPES.DELETE_APPOINTMENT):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_APPOINTMENTS):
    case SUCCESS(ACTION_TYPES.FETCH_APPOINTMENT_LIST):
      return {
        ...state,
        loading: false,
        totalItems: action.payload.headers['x-total-count'],
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_APPOINTMENT):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_APPOINTMENT):
    case SUCCESS(ACTION_TYPES.UPDATE_APPOINTMENT):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_APPOINTMENT):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: {}
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState
      };
    default:
      return state;
  }
};

const apiUrl = 'api/appointments';
const apiSearchUrl = 'api/_search/appointments';

// Actions

export const getSearchEntities: ICrudSearchAction<IAppointment> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_APPOINTMENTS,
  payload: axios.get<IAppointment>(`${apiSearchUrl}?query=${query}${sort ? `&page=${page}&size=${size}&sort=${sort}` : ''}`)
});

export const getEntities: ICrudGetAllAction<IAppointment> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_APPOINTMENT_LIST,
    payload: axios.get<IAppointment>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntity: ICrudGetAction<IAppointment> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_APPOINTMENT,
    payload: axios.get<IAppointment>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IAppointment> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_APPOINTMENT,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IAppointment> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_APPOINTMENT,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const deleteEntity: ICrudDeleteAction<IAppointment> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_APPOINTMENT,
    payload: axios.delete(requestUrl)
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
