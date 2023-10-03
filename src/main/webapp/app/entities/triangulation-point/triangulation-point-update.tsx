import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { ITriangulationReport } from 'app/shared/model/triangulation-report.model';
import { getEntities as getTriangulationReports } from 'app/entities/triangulation-report/triangulation-report.reducer';
import { IFrequency } from 'app/shared/model/frequency.model';
import { getEntities as getFrequencies } from 'app/entities/frequency/frequency.reducer';
import { ITriangulationPoint } from 'app/shared/model/triangulation-point.model';
import { getEntity, updateEntity, createEntity, reset } from './triangulation-point.reducer';

export const TriangulationPointUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const triangulationReports = useAppSelector(state => state.triangulationReport.entities);
  const frequencies = useAppSelector(state => state.frequency.entities);
  const triangulationPointEntity = useAppSelector(state => state.triangulationPoint.entity);
  const loading = useAppSelector(state => state.triangulationPoint.loading);
  const updating = useAppSelector(state => state.triangulationPoint.updating);
  const updateSuccess = useAppSelector(state => state.triangulationPoint.updateSuccess);

  const handleClose = () => {
    navigate('/triangulation-point' + location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getTriangulationReports({}));
    dispatch(getFrequencies({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    values.date = convertDateTimeToServer(values.date);

    const entity = {
      ...triangulationPointEntity,
      ...values,
      triangulationReport: triangulationReports.find(it => it.id.toString() === values.triangulationReport.toString()),
      frequency: frequencies.find(it => it.id.toString() === values.frequency.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          date: displayDefaultDateTime(),
        }
      : {
          ...triangulationPointEntity,
          date: convertDateTimeFromServer(triangulationPointEntity.date),
          triangulationReport: triangulationPointEntity?.triangulationReport?.id,
          frequency: triangulationPointEntity?.frequency?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="intelligenceApp.triangulationPoint.home.createOrEditLabel" data-cy="TriangulationPointCreateUpdateHeading">
            <Translate contentKey="intelligenceApp.triangulationPoint.home.createOrEditLabel">
              Create or edit a TriangulationPoint
            </Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="triangulation-point-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('intelligenceApp.triangulationPoint.description')}
                id="triangulation-point-description"
                name="description"
                data-cy="description"
                type="text"
              />
              <ValidatedField
                label={translate('intelligenceApp.triangulationPoint.longitude')}
                id="triangulation-point-longitude"
                name="longitude"
                data-cy="longitude"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  max: { value: 360, message: translate('entity.validation.max', { max: 360 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('intelligenceApp.triangulationPoint.latitude')}
                id="triangulation-point-latitude"
                name="latitude"
                data-cy="latitude"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  max: { value: 360, message: translate('entity.validation.max', { max: 360 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('intelligenceApp.triangulationPoint.date')}
                id="triangulation-point-date"
                name="date"
                data-cy="date"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                id="triangulation-point-triangulationReport"
                name="triangulationReport"
                data-cy="triangulationReport"
                label={translate('intelligenceApp.triangulationPoint.triangulationReport')}
                type="select"
              >
                <option value="" key="0" />
                {triangulationReports
                  ? triangulationReports.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="triangulation-point-frequency"
                name="frequency"
                data-cy="frequency"
                label={translate('intelligenceApp.triangulationPoint.frequency')}
                type="select"
              >
                <option value="" key="0" />
                {frequencies
                  ? frequencies.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/triangulation-point" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default TriangulationPointUpdate;
