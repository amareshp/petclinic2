import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './vet.reducer';
import { IVet } from 'app/shared/model/vet.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IVetDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class VetDetail extends React.Component<IVetDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { vetEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            Vet [<b>{vetEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="name">Name</span>
            </dt>
            <dd>{vetEntity.name}</dd>
            <dt>
              <span id="address">Address</span>
            </dt>
            <dd>{vetEntity.address}</dd>
            <dt>
              <span id="city">City</span>
            </dt>
            <dd>{vetEntity.city}</dd>
            <dt>
              <span id="stateProvince">State Province</span>
            </dt>
            <dd>{vetEntity.stateProvince}</dd>
            <dt>
              <span id="phone">Phone</span>
            </dt>
            <dd>{vetEntity.phone}</dd>
          </dl>
          <Button tag={Link} to="/entity/vet" replace color="info">
            <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/vet/${vetEntity.id}/edit`} replace color="primary">
            <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
          </Button>
        </Col>
      </Row>
    );
  }
}

const mapStateToProps = ({ vet }: IRootState) => ({
  vetEntity: vet.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(VetDetail);
