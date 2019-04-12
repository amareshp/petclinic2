import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Appointment from './appointment';
import AppointmentDetail from './appointment-detail';
import AppointmentUpdate from './appointment-update';
import AppointmentDeleteDialog from './appointment-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={AppointmentUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={AppointmentUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={AppointmentDetail} />
      <ErrorBoundaryRoute path={match.url} component={Appointment} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={AppointmentDeleteDialog} />
  </>
);

export default Routes;
