import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Slot from './slot';
import SlotDetail from './slot-detail';
import SlotUpdate from './slot-update';
import SlotDeleteDialog from './slot-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={SlotUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={SlotUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={SlotDetail} />
      <ErrorBoundaryRoute path={match.url} component={Slot} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={SlotDeleteDialog} />
  </>
);

export default Routes;
