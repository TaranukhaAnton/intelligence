import dayjs from 'dayjs';

import { APP_LOCAL_DATETIME_FORMAT, APP_LOCAL_DATETIME_FORMAT2 } from 'app/config/constants';

export const convertDateTimeFromServer = date => (date ? dayjs(date).format(APP_LOCAL_DATETIME_FORMAT) : null);
export const convertDateTimeFromServer2 = date => (date ? dayjs(date).format(APP_LOCAL_DATETIME_FORMAT2) : null);

export const convertDateTimeToServer = date => (date ? dayjs(date).toDate() : null);

export const displayDefaultDateTime = () => dayjs().startOf('day').format(APP_LOCAL_DATETIME_FORMAT);

export const encodedDate = date => {
  const formatedDate = dayjs(date).startOf('day').format(`${APP_LOCAL_DATETIME_FORMAT}+03:00`);
  return encodeURIComponent(formatedDate);
};
