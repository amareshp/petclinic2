import React from 'react';
import { DropdownItem } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { NavLink as Link } from 'react-router-dom';
import { NavDropdown } from '../header-components';

export const EntitiesMenu = props => (
  // tslint:disable-next-line:jsx-self-close
  <NavDropdown icon="th-list" name="Entities" id="entity-menu">
    <DropdownItem tag={Link} to="/entity/owner">
      <FontAwesomeIcon icon="asterisk" fixedWidth />
      &nbsp;Owner
    </DropdownItem>
    <DropdownItem tag={Link} to="/entity/pet">
      <FontAwesomeIcon icon="asterisk" fixedWidth />
      &nbsp;Pet
    </DropdownItem>
    <DropdownItem tag={Link} to="/entity/vet">
      <FontAwesomeIcon icon="asterisk" fixedWidth />
      &nbsp;Vet
    </DropdownItem>
    <DropdownItem tag={Link} to="/entity/slot">
      <FontAwesomeIcon icon="asterisk" fixedWidth />
      &nbsp;Slot
    </DropdownItem>
    <DropdownItem tag={Link} to="/entity/appointment">
      <FontAwesomeIcon icon="asterisk" fixedWidth />
      &nbsp;Appointment
    </DropdownItem>
    {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
  </NavDropdown>
);
