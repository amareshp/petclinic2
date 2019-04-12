import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './slot.reducer';
import { ISlot } from 'app/shared/model/slot.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface ISlotDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class SlotDetail extends React.Component<ISlotDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { slotEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            Slot [<b>{slotEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="startTime">Start Time</span>
            </dt>
            <dd>{slotEntity.startTime}</dd>
          </dl>
          <Button tag={Link} to="/entity/slot" replace color="info">
            <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/slot/${slotEntity.id}/edit`} replace color="primary">
            <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
          </Button>
        </Col>
      </Row>
    );
  }
}

const mapStateToProps = ({ slot }: IRootState) => ({
  slotEntity: slot.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(SlotDetail);
