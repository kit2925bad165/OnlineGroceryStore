const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
const backendDateTimePattern = /^(\d{4})-(\d{2})-(\d{2})[T ](\d{2}):(\d{2})(?::(\d{2})(?:\.\d+)?)?$/;

export function formatBackendDateTime(value) {
  if (typeof value !== 'string' || !value.trim()) return 'Date unavailable';

  const match = value.trim().match(backendDateTimePattern);
  if (!match) return value;

  const [, year, month, day, hour, minute, second = '00'] = match;
  const yearNumber = Number(year);
  const monthNumber = Number(month);
  const dayNumber = Number(day);
  const hourNumber = Number(hour);
  const minuteNumber = Number(minute);
  const secondNumber = Number(second);
  const isLeapYear = yearNumber % 4 === 0 && (yearNumber % 100 !== 0 || yearNumber % 400 === 0);
  const daysInMonth = [31, isLeapYear ? 29 : 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31][monthNumber - 1];

  if (!daysInMonth || dayNumber < 1 || dayNumber > daysInMonth || hourNumber > 23 || minuteNumber > 59 || secondNumber > 59) {
    return value;
  }

  const period = hourNumber < 12 ? 'AM' : 'PM';
  const displayHour = hourNumber % 12 || 12;
  return `${day} ${months[monthNumber - 1]} ${year}, ${String(displayHour).padStart(2, '0')}:${minute} ${period}`;
}