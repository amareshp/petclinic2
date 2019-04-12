import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IOwner } from 'app/shared/model/owner.model';
import { getEntities as getOwners } from 'app/entities/owner/owner.reducer';
import { getEntity, updateEntity, createEntity, reset } from './pet.reducer';
import { IPet } from 'app/shared/model/pet.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer, convertDateTimeToServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IPetUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IPetUpdateState {
  isNew: boolean;
  ownerId: string;
}

export class PetUpdate extends React.Component<IPetUpdateProps, IPetUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      ownerId: '0',
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

    this.props.getOwners();
  }

  saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const { petEntity } = this.props;
      const entity = {
        ...petEntity,
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
    this.props.history.push('/entity/pet');
  };

  render() {
    const { petEntity, owners, loading, updating } = this.props;
    const { isNew } = this.state;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="petclinic2App.pet.home.createOrEditLabel">Create or edit a Pet</h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : petEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="id">ID</Label>
                    <AvInput id="pet-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="nameLabel" for="name">
                    Name
                  </Label>
                  <AvField id="pet-name" type="text" name="name" />
                </AvGroup>
                <AvGroup>
                  <Label id="typeLabel" for="type">
                    Type
                  </Label>
                  <AvField id="pet-type" type="text" name="type" />
                </AvGroup>
                <AvGroup>
                  <Label id="breedLabel" for="breed">
                    Breed
                  </Label>
                  <AvField id="pet-breed" type="text" name="breed" />
                </AvGroup>
                <AvGroup>
                  <Label for="owner.name">Owner</Label>
                  <AvInput id="pet-owner" type="select" className="form-control" name="owner.id">
                    <option value="" key="0" />
                    {owners
                      ? owners.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.name}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <Button tag={Link} id="cancel-save" to="/entity/pet" replace color="info">
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
  owners: storeState.owner.entities,
  petEntity: storeState.pet.entity,
  loading: storeState.pet.loading,
  updating: storeState.pet.updating,
  updateSuccess: storeState.pet.updateSuccess
});

const mapDispatchToProps = {
  getOwners,
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
)(PetUpdate);
