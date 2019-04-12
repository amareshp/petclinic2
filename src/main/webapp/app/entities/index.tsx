import React from 'react';
import { Switch } from 'react-router-dom';

// tslint:disable-next-line:no-unused-variable
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Owner from './owner';
import Pet from './pet';
import Vet from './vet';
import Slot from './slot';
import Appointment from './appointment';
/* jhipster-needle-add-route-import - JHipster will add routes here */

const Routes = ({ match }) => (
  <div>
    <Switch>
      {/* prettier-ignore */}
      <ErrorBoundaryRoute path={`${match.url}/owner`} component={Owner} />
      <ErrorBoundaryRoute path={`${match.url}/pet`} component={Pet} />
      <ErrorBoundaryRoute path={`${match.url}/vet`} component={Vet} />
      <ErrorBoundaryRoute path={`${match.url}/slot`} component={Slot} />
      <ErrorBoundaryRoute path={`${match.url}/appointment`} component={Appointment} />
      {/* jhipster-needle-add-route-path - JHipster will routes here */}
    </Switch>
  </div>
);

export default Routes;
