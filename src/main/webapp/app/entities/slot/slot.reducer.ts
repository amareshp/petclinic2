import axios from 'axios';
import { ICrudSearchAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { ISlot, defaultValue } from 'app/shared/model/slot.model';

export const ACTION_TYPES = {
  SEARCH_SLOTS: 'slot/SEARCH_SLOTS',
  FETCH_SLOT_LIST: 'slot/FETCH_SLOT_LIST',
  FETCH_SLOT: 'slot/FETCH_SLOT',
  CREATE_SLOT: 'slot/CREATE_SLOT',
  UPDATE_SLOT: 'slot/UPDATE_SLOT',
  DELETE_SLOT: 'slot/DELETE_SLOT',
  RESET: 'slot/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<ISlot>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false
};

export type SlotState = Readonly<typeof initialState>;

// Reducer

export default (state: SlotState = initialState, action): SlotState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_SLOTS):
    case REQUEST(ACTION_TYPES.FETCH_SLOT_LIST):
    case REQUEST(ACTION_TYPES.FETCH_SLOT):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_SLOT):
    case REQUEST(ACTION_TYPES.UPDATE_SLOT):
    case REQUEST(ACTION_TYPES.DELETE_SLOT):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_SLOTS):
    case FAILURE(ACTION_TYPES.FETCH_SLOT_LIST):
    case FAILURE(ACTION_TYPES.FETCH_SLOT):
    case FAILURE(ACTION_TYPES.CREATE_SLOT):
    case FAILURE(ACTION_TYPES.UPDATE_SLOT):
    case FAILURE(ACTION_TYPES.DELETE_SLOT):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_SLOTS):
    case SUCCESS(ACTION_TYPES.FETCH_SLOT_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_SLOT):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_SLOT):
    case SUCCESS(ACTION_TYPES.UPDATE_SLOT):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_SLOT):
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

const apiUrl = 'api/slots';
const apiSearchUrl = 'api/_search/slots';

// Actions

export const getSearchEntities: ICrudSearchAction<ISlot> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_SLOTS,
  payload: axios.get<ISlot>(`${apiSearchUrl}?query=${query}`)
});

export const getEntities: ICrudGetAllAction<ISlot> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_SLOT_LIST,
  payload: axios.get<ISlot>(`${apiUrl}?cacheBuster=${new Date().getTime()}`)
});

export const getEntity: ICrudGetAction<ISlot> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_SLOT,
    payload: axios.get<ISlot>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<ISlot> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_SLOT,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<ISlot> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_SLOT,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const deleteEntity: ICrudDeleteAction<ISlot> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_SLOT,
    payload: axios.delete(requestUrl)
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
