import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { ISlot } from 'app/shared/model/slot.model';
import { getEntities as getSlots } from 'app/entities/slot/slot.reducer';
import { IVet } from 'app/shared/model/vet.model';
import { getEntities as getVets } from 'app/entities/vet/vet.reducer';
import { IPet } from 'app/shared/model/pet.model';
import { getEntities as getPets } from 'app/entities/pet/pet.reducer';
import { getEntity, updateEntity, createEntity, reset } from './appointment.reducer';
import { IAppointment } from 'app/shared/model/appointment.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer, convertDateTimeToServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IAppointmentUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IAppointmentUpdateState {
  isNew: boolean;
  slotId: string;
  vetId: string;
  petId: string;
}

export class AppointmentUpdate extends React.Component<IAppointmentUpdateProps, IAppointmentUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      slotId: '0',
      vetId: '0',
      petId: '0',
      isNew: !this.props.match.params || !this.props.match.params.id
    };
  }

  componentWillUpdate(nextProps, nextState) {
    if (nextProps.updateSuccess !== this.props.updateSuccess && nextProps.updateSuccess) {
      this.handleClose();
    }
  }

  componentDidMount() {
    if (this.state.isNew) {
      this.props.reset();
    } else {
      this.props.getEntity(this.props.match.params.id);
    }

    this.props.getSlots();
    this.props.getVets();
    this.props.getPets();
  }

  saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const { appointmentEntity } = this.props;
      const entity = {
        ...appointmentEntity,
        ...values
      };

      if (this.state.isNew) {
        this.props.createEntity(entity);
      } else {
        this.props.updateEntity(entity);
      }
    }
  };

  handleClose = () => {
    this.props.history.push('/entity/appointment');
  };

  render() {
    const { appointmentEntity, slots, vets, pets, loading, updating } = this.props;
    const { isNew } = this.state;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="petclinic2App.appointment.home.createOrEditLabel">Create or edit a Appointment</h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : appointmentEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="id">ID</Label>
                    <AvInput id="appointment-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="apptTimeLabel" for="apptTime">
                    Appt Time
                  </Label>
                  <AvField
                    id="appointment-apptTime"
                    type="date"
                    className="form-control"
                    name="apptTime"
                    validate={{
                      required: { value: true, errorMessage: 'This field is required.' }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label for="slot.startTime">Slot</Label>
                  <AvInput id="appointment-slot" type="select" className="form-control" name="slot.id">
                    <option value="" key="0" />
                    {slots
                      ? slots.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.startTime}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label for="vet.name">Vet</Label>
                  <AvInput id="appointment-vet" type="select" className="form-control" name="vet.id">
                    <option value="" key="0" />
                    {vets
                      ? vets.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.name}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label for="pet.name">Pet</Label>
                  <AvInput id="appointment-pet" type="select" className="form-control" name="pet.id">
                    <option value="" key="0" />
                    {pets
                      ? pets.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.name}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <Button tag={Link} id="cancel-save" to="/entity/appointment" replace color="info">
                  <FontAwesomeIcon icon="arrow-left" />
                  &nbsp;
                  <span className="d-none d-md-inline">Back</span>
                </Button>
                &nbsp;
                <Button color="primary" id="save-entity" type="submit" disabled={updating}>
                  <FontAwesomeIcon icon="save" />
                  &nbsp; Save
                </Button>
              </AvForm>
            )}
          </Col>
        </Row>
      </div>
    );
  }
}

const mapStateToProps = (storeState: IRootState) => ({
  slots: storeState.slot.entities,
  vets: storeState.vet.entities,
  pets: storeState.pet.entities,
  appointmentEntity: storeState.appointment.entity,
  loading: storeState.appointment.loading,
  updating: storeState.appointment.updating,
  updateSuccess: storeState.appointment.updateSuccess
});

const mapDispatchToProps = {
  getSlots,
  getVets,
  getPets,
  getEntity,
  updateEntity,
  createEntity,
  reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(AppointmentUpdate);
