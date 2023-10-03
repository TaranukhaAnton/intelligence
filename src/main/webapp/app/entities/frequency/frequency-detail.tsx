import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './frequency.reducer';

export const FrequencyDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const frequencyEntity = useAppSelector(state => state.frequency.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="frequencyDetailsHeading">
          <Translate contentKey="intelligenceApp.frequency.detail.title">Frequency</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{frequencyEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="intelligenceApp.frequency.name">Name</Translate>
            </span>
          </dt>
          <dd>{frequencyEntity.name}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="intelligenceApp.frequency.description">Description</Translate>
            </span>
          </dt>
          <dd>{frequencyEntity.description}</dd>
        </dl>
        <Button tag={Link} to="/frequency" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/frequency/${frequencyEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default FrequencyDetail;
