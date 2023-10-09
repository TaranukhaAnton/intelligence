import React, { useState } from 'react';

import DatePicker, { DatePickerProps } from 'react-multi-date-picker';
import InputIcon from 'react-multi-date-picker/components/input_icon';
import { FormGroup, Label, Input, InputProps } from 'reactstrap';

interface IDatapicker {
  labelText?: string;
  value?: any;
  isChecked?: boolean;
  onChange?: (value: any) => void;
  onSwitch?: (value: any) => void;
}

const Datepicker = ({ labelText, value, isChecked, onSwitch, onChange }: IDatapicker) => {
  return (
    <div className="date-picker-wrapper">
      <FormGroup switch>
        <Input type="switch" onClick={onSwitch} />
        <Label check>{labelText}</Label>
      </FormGroup>
      {isChecked && (
        <DatePicker render={<InputIcon />} value={value} onChange={onChange} format="MM/DD/YYYY" className="date-from-picker" />
      )}
    </div>
  );
};

export default Datepicker;
