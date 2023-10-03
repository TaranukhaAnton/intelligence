import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { ITriangulationReport } from 'app/shared/model/triangulation-report.model';
import { getEntity, updateEntity, createEntity, reset } from './triangulation-report.reducer';

export const TriangulationReportUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const triangulationReportEntity = useAppSelector(state => state.triangulationReport.entity);
  const loading = useAppSelector(state => state.triangulationReport.loading);
  const updating = useAppSelector(state => state.triangulationReport.updating);
  const updateSuccess = useAppSelector(state => state.triangulationReport.updateSuccess);

  const handleClose = () => {
    navigate('/triangulation-report' + location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    values.date = convertDateTimeToServer(values.date);

    const entity = {
      ...triangulationReportEntity,
      ...values,
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
          ...triangulationReportEntity,
          date: convertDateTimeFromServer(triangulationReportEntity.date),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="intelligenceApp.triangulationReport.home.createOrEditLabel" data-cy="TriangulationReportCreateUpdateHeading">
            <Translate contentKey="intelligenceApp.triangulationReport.home.createOrEditLabel">
              Create or edit a TriangulationReport
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
                  id="triangulation-report-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('intelligenceApp.triangulationReport.date')}
                id="triangulation-report-date"
                name="date"
                data-cy="date"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('intelligenceApp.triangulationReport.name')}
                id="triangulation-report-name"
                name="name"
                data-cy="name"
                type="text"
              />
              <ValidatedField
                label={translate('intelligenceApp.triangulationReport.conclusion')}
                id="triangulation-report-conclusion"
                name="conclusion"
                data-cy="conclusion"
                type="text"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/triangulation-report" replace color="info">
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

export default TriangulationReportUpdate;
