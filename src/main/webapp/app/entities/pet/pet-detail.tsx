import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './pet.reducer';
import { IPet } from 'app/shared/model/pet.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IPetDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class PetDetail extends React.Component<IPetDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { petEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            Pet [<b>{petEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="name">Name</span>
            </dt>
            <dd>{petEntity.name}</dd>
            <dt>
              <span id="type">Type</span>
            </dt>
            <dd>{petEntity.type}</dd>
            <dt>
              <span id="breed">Breed</span>
            </dt>
            <dd>{petEntity.breed}</dd>
            <dt>Owner</dt>
            <dd>{petEntity.owner ? petEntity.owner.name : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/pet" replace color="info">
            <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/pet/${petEntity.id}/edit`} replace color="primary">
            <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
          </Button>
        </Col>
      </Row>
    );
  }
}

const mapStateToProps = ({ pet }: IRootState) => ({
  petEntity: pet.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(PetDetail);
