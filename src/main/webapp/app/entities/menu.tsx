import React from 'react';
import { Translate } from 'react-jhipster';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/triangulation-point">
        <Translate contentKey="global.menu.entities.triangulationPoint" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/frequency">
        <Translate contentKey="global.menu.entities.frequency" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/triangulation-report">
        <Translate contentKey="global.menu.entities.triangulationReport" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
