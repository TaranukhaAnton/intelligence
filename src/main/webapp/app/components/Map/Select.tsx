import React from 'react';

const Select = ({ data, onChange, className }) => {
  return (
    <select name="frq-select" onChange={onChange} className={className}>
      <option value="">Виберіть частоту</option>
      {data.map(item => {
        return (
          <option key={item.id} value={item.id}>
            {item.name}
          </option>
        );
      })}
    </select>
  );
};

export default Select;
