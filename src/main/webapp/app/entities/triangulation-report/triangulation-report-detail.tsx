import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './triangulation-report.reducer';

export const TriangulationReportDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const triangulationReportEntity = useAppSelector(state => state.triangulationReport.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="triangulationReportDetailsHeading">
          <Translate contentKey="intelligenceApp.triangulationReport.detail.title">TriangulationReport</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{triangulationReportEntity.id}</dd>
          <dt>
            <span id="date">
              <Translate contentKey="intelligenceApp.triangulationReport.date">Date</Translate>
            </span>
          </dt>
          <dd>
            {triangulationReportEntity.date ? (
              <TextFormat value={triangulationReportEntity.date} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="name">
              <Translate contentKey="intelligenceApp.triangulationReport.name">Name</Translate>
            </span>
          </dt>
          <dd>{triangulationReportEntity.name}</dd>
          <dt>
            <span id="conclusion">
              <Translate contentKey="intelligenceApp.triangulationReport.conclusion">Conclusion</Translate>
            </span>
          </dt>
          <dd>{triangulationReportEntity.conclusion}</dd>
        </dl>
        <Button tag={Link} to="/triangulation-report" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/triangulation-report/${triangulationReportEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default TriangulationReportDetail;
