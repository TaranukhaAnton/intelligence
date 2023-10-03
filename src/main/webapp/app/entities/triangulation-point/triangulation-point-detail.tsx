import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './triangulation-point.reducer';

export const TriangulationPointDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const triangulationPointEntity = useAppSelector(state => state.triangulationPoint.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="triangulationPointDetailsHeading">
          <Translate contentKey="intelligenceApp.triangulationPoint.detail.title">TriangulationPoint</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{triangulationPointEntity.id}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="intelligenceApp.triangulationPoint.description">Description</Translate>
            </span>
          </dt>
          <dd>{triangulationPointEntity.description}</dd>
          <dt>
            <span id="longitude">
              <Translate contentKey="intelligenceApp.triangulationPoint.longitude">Longitude</Translate>
            </span>
          </dt>
          <dd>{triangulationPointEntity.longitude}</dd>
          <dt>
            <span id="latitude">
              <Translate contentKey="intelligenceApp.triangulationPoint.latitude">Latitude</Translate>
            </span>
          </dt>
          <dd>{triangulationPointEntity.latitude}</dd>
          <dt>
            <span id="date">
              <Translate contentKey="intelligenceApp.triangulationPoint.date">Date</Translate>
            </span>
          </dt>
          <dd>
            {triangulationPointEntity.date ? (
              <TextFormat value={triangulationPointEntity.date} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="intelligenceApp.triangulationPoint.triangulationReport">Triangulation Report</Translate>
          </dt>
          <dd>{triangulationPointEntity.triangulationReport ? triangulationPointEntity.triangulationReport.id : ''}</dd>
          <dt>
            <Translate contentKey="intelligenceApp.triangulationPoint.frequency">Frequency</Translate>
          </dt>
          <dd>{triangulationPointEntity.frequency ? triangulationPointEntity.frequency.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/triangulation-point" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/triangulation-point/${triangulationPointEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default TriangulationPointDetail;
