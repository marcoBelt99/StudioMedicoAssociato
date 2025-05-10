(function (global, factory) {
  typeof exports === 'object' && typeof module !== 'undefined' ? factory(exports) :
  typeof define === 'function' && define.amd ? define(['exports'], factory) :
  (global = typeof globalThis !== 'undefined' ? globalThis : global || self, factory(global.SXDragAndDrop = {}));
})(this, (function (exports) { 'use strict';

  var PluginName;
  (function (PluginName) {
      PluginName["DragAndDrop"] = "dragAndDrop";
      PluginName["EventModal"] = "eventModal";
      PluginName["ScrollController"] = "scrollController";
      PluginName["EventRecurrence"] = "eventRecurrence";
      PluginName["Resize"] = "resize";
      PluginName["CalendarControls"] = "calendarControls";
      PluginName["CurrentTime"] = "currentTime";
  })(PluginName || (PluginName = {}));

  class InvalidTimeStringError extends Error {
      constructor(timeString) {
          super(`Invalid time string: ${timeString}`);
      }
  }

  // regex for strings between 00:00 and 23:59
  const timeStringRegex = /^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$/;
  const dateTimeStringRegex = /^(\d{4})-(\d{2})-(\d{2}) (0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$/;
  const dateStringRegex = /^(\d{4})-(\d{2})-(\d{2})$/;

  const DateFormats = {
      DATE_STRING: /^\d{4}-\d{2}-\d{2}$/,
      DATE_TIME_STRING: /^\d{4}-\d{2}-\d{2} \d{2}:\d{2}$/,
  };

  class InvalidDateTimeError extends Error {
      constructor(dateTimeSpecification) {
          super(`Invalid date time specification: ${dateTimeSpecification}`);
      }
  }

  const toJSDate = (dateTimeSpecification) => {
      if (!DateFormats.DATE_TIME_STRING.test(dateTimeSpecification) &&
          !DateFormats.DATE_STRING.test(dateTimeSpecification))
          throw new InvalidDateTimeError(dateTimeSpecification);
      return new Date(Number(dateTimeSpecification.slice(0, 4)), Number(dateTimeSpecification.slice(5, 7)) - 1, Number(dateTimeSpecification.slice(8, 10)), Number(dateTimeSpecification.slice(11, 13)), // for date strings this will be 0
      Number(dateTimeSpecification.slice(14, 16)) // for date strings this will be 0
      );
  };
  const toIntegers = (dateTimeSpecification) => {
      const hours = dateTimeSpecification.slice(11, 13), minutes = dateTimeSpecification.slice(14, 16);
      return {
          year: Number(dateTimeSpecification.slice(0, 4)),
          month: Number(dateTimeSpecification.slice(5, 7)) - 1,
          date: Number(dateTimeSpecification.slice(8, 10)),
          hours: hours !== '' ? Number(hours) : undefined,
          minutes: minutes !== '' ? Number(minutes) : undefined,
      };
  };

  class NumberRangeError extends Error {
      constructor(min, max) {
          super(`Number must be between ${min} and ${max}.`);
          Object.defineProperty(this, "min", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: min
          });
          Object.defineProperty(this, "max", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: max
          });
      }
  }

  const doubleDigit = (number) => {
      if (number < 0 || number > 99)
          throw new NumberRangeError(0, 99);
      return String(number).padStart(2, '0');
  };

  const toDateString = (date) => {
      return `${date.getFullYear()}-${doubleDigit(date.getMonth() + 1)}-${doubleDigit(date.getDate())}`;
  };
  const toTimeString = (date) => {
      return `${doubleDigit(date.getHours())}:${doubleDigit(date.getMinutes())}`;
  };
  const toDateTimeString = (date) => {
      return `${toDateString(date)} ${toTimeString(date)}`;
  };

  const minuteTimePointMultiplier = 1.6666666666666667; // 100 / 60
  const timePointsFromString = (timeString) => {
      if (!timeStringRegex.test(timeString) && timeString !== '24:00')
          throw new InvalidTimeStringError(timeString);
      const [hoursInt, minutesInt] = timeString
          .split(':')
          .map((time) => parseInt(time, 10));
      let minutePoints = (minutesInt * minuteTimePointMultiplier).toString();
      if (minutePoints.split('.')[0].length < 2)
          minutePoints = `0${minutePoints}`;
      return Number(hoursInt + minutePoints);
  };
  const addTimePointsToDateTime = (dateTimeString, pointsToAdd) => {
      const minutesToAdd = pointsToAdd / minuteTimePointMultiplier;
      const jsDate = toJSDate(dateTimeString);
      jsDate.setMinutes(jsDate.getMinutes() + minutesToAdd);
      return toDateTimeString(jsDate);
  };

  const addDays = (to, nDays) => {
      const { year, month, date, hours, minutes } = toIntegers(to);
      const isDateTimeString = hours !== undefined && minutes !== undefined;
      const jsDate = new Date(year, month, date, hours !== null && hours !== void 0 ? hours : 0, minutes !== null && minutes !== void 0 ? minutes : 0);
      jsDate.setDate(jsDate.getDate() + nDays);
      if (isDateTimeString) {
          return toDateTimeString(jsDate);
      }
      return toDateString(jsDate);
  };

  const dateFromDateTime = (dateTime) => {
      return dateTime.slice(0, 10);
  };
  const timeFromDateTime = (dateTime) => {
      return dateTime.slice(11);
  };

  const setDateInDateTimeString = (dateTimeString, newDate) => {
      const timeCache = timeFromDateTime(dateTimeString);
      return `${newDate} ${timeCache}`;
  };

  const getTimeGridEventCopyElementId = (id) => {
      return 'time-grid-event-copy-' + id;
  };

  const updateRecurringEvent = ($app, eventCopy, startPreDrag) => {
      var _a;
      (_a = $app.config.plugins.eventRecurrence) === null || _a === void 0 ? void 0 : _a.updateRecurrenceDND(eventCopy.id, startPreDrag, eventCopy.start);
  };
  const updateNonRecurringEvent = ($app, eventCopy) => {
      const eventToUpdate = $app.calendarEvents.list.value.find((event) => event.id === eventCopy.id);
      if (!eventToUpdate)
          return;
      eventToUpdate.start = eventCopy.start;
      eventToUpdate.end = eventCopy.end;
      $app.calendarEvents.list.value = [...$app.calendarEvents.list.value];
  };
  const updateDraggedEvent = ($app, eventCopy, startPreDrag) => {
      if ('rrule' in eventCopy._getForeignProperties() &&
          $app.config.plugins.eventRecurrence) {
          updateRecurringEvent($app, eventCopy, startPreDrag);
      }
      else {
          updateNonRecurringEvent($app, eventCopy);
      }
      if ($app.config.callbacks.onEventUpdate) {
          $app.config.callbacks.onEventUpdate(eventCopy._getExternalEvent());
      }
  };

  const isUIEventTouchEvent = (event) => {
      return 'touches' in event && typeof event.touches === 'object';
  };

  const getEventCoordinates = (uiEvent) => {
      const actualEvent = isUIEventTouchEvent(uiEvent)
          ? uiEvent.touches[0]
          : uiEvent;
      return {
          clientX: actualEvent.clientX,
          clientY: actualEvent.clientY,
      };
  };

  const getTimePointsPerPixel = ($app) => {
      return $app.config.timePointsPerDay / $app.config.weekOptions.value.gridHeight;
  };

  const testIfShouldAbort = ($app, 
  /**
   * For the month grid the original event is used, since there is no copy created for dragging.
   * For other views, a copy is used, hence the name of this parameter.
   * */
  eventCopyOrOriginalEvent, originalStart, originalEnd, updateCopy) => {
      const onBeforeEventUpdate = $app.config.callbacks.onBeforeEventUpdate;
      if (onBeforeEventUpdate) {
          const oldEvent = eventCopyOrOriginalEvent._getExternalEvent();
          oldEvent.start = originalStart;
          oldEvent.end = originalEnd;
          const newEvent = eventCopyOrOriginalEvent._getExternalEvent();
          const validationResult = onBeforeEventUpdate(oldEvent, newEvent, $app);
          if (!validationResult) {
              updateCopy === null || updateCopy === void 0 ? void 0 : updateCopy(undefined);
              return true; // abort
          }
      }
      return false;
  };

  class TimeGridDragHandlerImpl {
      constructor($app, eventCoordinates, eventCopy, updateCopy, dayBoundariesDateTime, CHANGE_THRESHOLD_IN_TIME_POINTS) {
          Object.defineProperty(this, "$app", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: $app
          });
          Object.defineProperty(this, "eventCoordinates", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: eventCoordinates
          });
          Object.defineProperty(this, "eventCopy", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: eventCopy
          });
          Object.defineProperty(this, "updateCopy", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: updateCopy
          });
          Object.defineProperty(this, "dayBoundariesDateTime", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: dayBoundariesDateTime
          });
          Object.defineProperty(this, "CHANGE_THRESHOLD_IN_TIME_POINTS", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: CHANGE_THRESHOLD_IN_TIME_POINTS
          });
          Object.defineProperty(this, "dayWidth", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: void 0
          });
          Object.defineProperty(this, "startY", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: void 0
          });
          Object.defineProperty(this, "startX", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: void 0
          });
          Object.defineProperty(this, "lastIntervalDiff", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: 0
          });
          Object.defineProperty(this, "lastDaysDiff", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: 0
          });
          Object.defineProperty(this, "originalStart", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: void 0
          });
          Object.defineProperty(this, "originalEnd", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: void 0
          });
          Object.defineProperty(this, "handleMouseOrTouchMove", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: (uiEvent) => {
                  const { clientX, clientY } = getEventCoordinates(uiEvent);
                  const pixelDiffY = clientY - this.startY;
                  const timePointsDiffY = pixelDiffY * this.timePointsPerPixel();
                  const currentIntervalDiff = Math.round(timePointsDiffY / this.CHANGE_THRESHOLD_IN_TIME_POINTS);
                  const pixelDiffX = clientX - this.startX;
                  const currentDaysDiff = Math.round(pixelDiffX / this.dayWidth);
                  this.handleVerticalMouseOrTouchMove(currentIntervalDiff);
                  this.handleHorizontalMouseOrTouchMove(currentDaysDiff);
              }
          });
          Object.defineProperty(this, "handleMouseUpOrTouchEnd", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: () => {
                  document.removeEventListener('mousemove', this.handleMouseOrTouchMove);
                  document.removeEventListener('touchmove', this.handleMouseOrTouchMove);
                  document.removeEventListener('mouseup', this.handleMouseUpOrTouchEnd);
                  document.removeEventListener('touchend', this.handleMouseUpOrTouchEnd);
                  this.updateCopy(undefined);
                  const shouldAbort = testIfShouldAbort(this.$app, this.eventCopy, this.originalStart, this.originalEnd, this.updateCopy);
                  if (shouldAbort)
                      return;
                  this.updateOriginalEvent();
              }
          });
          this.dayWidth = $app.elements.calendarWrapper.querySelector('.sx__time-grid-day').clientWidth;
          this.startY = this.eventCoordinates.clientY;
          this.startX = this.eventCoordinates.clientX;
          this.originalStart = this.eventCopy.start;
          this.originalEnd = this.eventCopy.end;
          this.init();
      }
      init() {
          document.addEventListener('mousemove', this.handleMouseOrTouchMove);
          document.addEventListener('mouseup', this.handleMouseUpOrTouchEnd);
          document.addEventListener('touchmove', this.handleMouseOrTouchMove, {
              passive: false,
          });
          document.addEventListener('touchend', this.handleMouseUpOrTouchEnd);
      }
      timePointsPerPixel() {
          return getTimePointsPerPixel(this.$app);
      }
      handleVerticalMouseOrTouchMove(currentIntervalDiff) {
          if (currentIntervalDiff === this.lastIntervalDiff)
              return;
          const pointsToAdd = currentIntervalDiff > this.lastIntervalDiff
              ? this.CHANGE_THRESHOLD_IN_TIME_POINTS
              : -this.CHANGE_THRESHOLD_IN_TIME_POINTS;
          this.setTimeForEventCopy(pointsToAdd);
          this.lastIntervalDiff = currentIntervalDiff;
      }
      setTimeForEventCopy(pointsToAdd) {
          const newStart = addTimePointsToDateTime(this.eventCopy.start, pointsToAdd);
          const newEnd = addTimePointsToDateTime(this.eventCopy.end, pointsToAdd);
          let currentDiff = this.lastDaysDiff;
          if (this.$app.config.direction === 'rtl') {
              currentDiff = -currentDiff;
          }
          if (newStart < addDays(this.dayBoundariesDateTime.start, currentDiff))
              return;
          if (newEnd > addDays(this.dayBoundariesDateTime.end, currentDiff))
              return;
          this.eventCopy.start = newStart;
          this.eventCopy.end = newEnd;
          this.updateCopy(this.eventCopy);
      }
      handleHorizontalMouseOrTouchMove(totalDaysDiff) {
          if (totalDaysDiff === this.lastDaysDiff)
              return;
          let diffToAdd = totalDaysDiff - this.lastDaysDiff;
          if (this.$app.config.direction === 'rtl')
              diffToAdd = -diffToAdd;
          const newStartDate = addDays(dateFromDateTime(this.eventCopy.start), diffToAdd);
          const newEndDate = addDays(dateFromDateTime(this.eventCopy.end), diffToAdd);
          const newStart = setDateInDateTimeString(this.eventCopy.start, newStartDate);
          const newEnd = setDateInDateTimeString(this.eventCopy.end, newEndDate);
          if (newStart < this.$app.calendarState.range.value.start)
              return;
          if (newEnd > this.$app.calendarState.range.value.end)
              return;
          this.setDateForEventCopy(newStart, newEnd);
          this.transformEventCopyPosition(totalDaysDiff);
          this.lastDaysDiff = totalDaysDiff;
      }
      setDateForEventCopy(newStart, newEnd) {
          this.eventCopy.start = newStart;
          this.eventCopy.end = newEnd;
          this.updateCopy(this.eventCopy);
      }
      transformEventCopyPosition(totalDaysDiff) {
          const copyElement = this.$app.elements.calendarWrapper.querySelector('#' + getTimeGridEventCopyElementId(this.eventCopy.id));
          copyElement.style.transform = `translateX(calc(${totalDaysDiff * 100}% + ${totalDaysDiff}px))`;
      }
      updateOriginalEvent() {
          if (this.lastIntervalDiff === 0 && this.lastDaysDiff === 0)
              return;
          const dayIsSame = this.lastDaysDiff === 0;
          const eventElement = document.querySelector(`[data-event-id="${this.eventCopy.id}"]`);
          const shouldHideEventToPreventFlickering = !dayIsSame && eventElement instanceof HTMLElement;
          if (shouldHideEventToPreventFlickering)
              eventElement.style.display = 'none';
          updateDraggedEvent(this.$app, this.eventCopy, this.originalStart);
      }
  }

  const getTimeGridDayWidth = ($app) => {
      return $app.elements.calendarWrapper.querySelector('.sx__time-grid-day').clientWidth;
  };

  const getDateGridEventCopy = ($app, eventCopy) => {
      return $app.elements.calendarWrapper.querySelector('#' + getTimeGridEventCopyElementId(eventCopy.id));
  };

  const MS_PER_DAY = 1000 * 60 * 60 * 24;
  class DateGridDragHandlerImpl {
      constructor($app, eventCoordinates, eventCopy, updateCopy) {
          Object.defineProperty(this, "$app", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: $app
          });
          Object.defineProperty(this, "eventCopy", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: eventCopy
          });
          Object.defineProperty(this, "updateCopy", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: updateCopy
          });
          Object.defineProperty(this, "startX", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: void 0
          });
          Object.defineProperty(this, "dayWidth", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: void 0
          });
          Object.defineProperty(this, "originalStart", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: void 0
          });
          Object.defineProperty(this, "originalEnd", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: void 0
          });
          Object.defineProperty(this, "rangeStartDate", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: void 0
          });
          Object.defineProperty(this, "rangeEndDate", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: void 0
          });
          Object.defineProperty(this, "lastDaysDiff", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: 0
          });
          Object.defineProperty(this, "handleMouseOrTouchMove", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: (uiEvent) => {
                  const { clientX } = getEventCoordinates(uiEvent);
                  const pixelDiffX = clientX - this.startX;
                  let currentDaysDiff = Math.round(pixelDiffX / this.dayWidth);
                  if (this.$app.config.direction === 'rtl')
                      currentDaysDiff *= -1;
                  if (currentDaysDiff === this.lastDaysDiff)
                      return;
                  const newStart = addDays(this.originalStart, currentDaysDiff);
                  const newEnd = addDays(this.originalEnd, currentDaysDiff);
                  const newStartDate = dateFromDateTime(newStart);
                  const newEndDate = dateFromDateTime(newEnd);
                  if (newStartDate > this.rangeEndDate)
                      return;
                  if (newEndDate < this.rangeStartDate)
                      return;
                  this.eventCopy.start = newStart;
                  this.eventCopy.end = newEnd;
                  const newStartIsInWeek = newStartDate >= this.rangeStartDate && newStartDate <= this.rangeEndDate;
                  const firstDateInGrid = newStartIsInWeek
                      ? newStartDate
                      : this.rangeStartDate;
                  const lastDateIsInGrid = newEndDate >= this.rangeStartDate && newEndDate <= this.rangeEndDate;
                  const lastDateInGrid = lastDateIsInGrid ? newEndDate : this.rangeEndDate;
                  this.eventCopy._nDaysInGrid =
                      Math.round((new Date(lastDateInGrid).getTime() -
                          new Date(firstDateInGrid).getTime()) /
                          MS_PER_DAY) + 1;
                  /**
                   * Transitioning the position sideways is not necessary as long as the start date is earlier than the first date in the grid.
                   * While moving an event during a state as such, it will optically look as if its position is transitioned, since the event width is increased and decreased
                   * as the event is moved.
                   * */
                  if (newStartDate >= this.rangeStartDate)
                      this.transformEventCopyPosition(newStartDate);
                  this.updateCopy(this.eventCopy);
                  this.lastDaysDiff = currentDaysDiff;
              }
          });
          Object.defineProperty(this, "handleMouseUpOrTouchEnd", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: () => {
                  document.removeEventListener('mousemove', this.handleMouseOrTouchMove);
                  document.removeEventListener('touchmove', this.handleMouseOrTouchMove);
                  const shouldAbort = testIfShouldAbort(this.$app, this.eventCopy, this.originalStart, this.originalEnd, this.updateCopy);
                  if (shouldAbort)
                      return;
                  this.updateOriginalEvent();
                  setTimeout(() => {
                      this.updateCopy(undefined);
                  }, 10); // Timeout needed to prevent the original from being displayed for a split second, before being removed from DOM.
              }
          });
          this.startX = eventCoordinates.clientX;
          this.dayWidth = getTimeGridDayWidth(this.$app);
          this.originalStart = this.eventCopy.start;
          this.originalEnd = this.eventCopy.end;
          this.rangeStartDate = dateFromDateTime(this.$app.calendarState.range.value.start);
          this.rangeEndDate = addDays(this.rangeStartDate, $app.config.weekOptions.value.nDays - 1);
          this.init();
      }
      init() {
          document.addEventListener('mousemove', this.handleMouseOrTouchMove);
          document.addEventListener('mouseup', this.handleMouseUpOrTouchEnd, {
              once: true,
          });
          document.addEventListener('touchmove', this.handleMouseOrTouchMove, {
              passive: false,
          });
          document.addEventListener('touchend', this.handleMouseUpOrTouchEnd, {
              once: true,
          });
      }
      transformEventCopyPosition(newStartDate) {
          const dateFromOriginalStart = dateFromDateTime(this.originalStart);
          let daysToShift = Math.round((new Date(newStartDate).getTime() -
              new Date(dateFromOriginalStart >= this.rangeStartDate
                  ? dateFromOriginalStart
                  : this.rangeStartDate).getTime()) /
              MS_PER_DAY);
          if (this.$app.config.direction === 'rtl')
              daysToShift *= -1;
          getDateGridEventCopy(this.$app, this.eventCopy).style.transform =
              `translateX(calc(${daysToShift * this.dayWidth}px + ${daysToShift}px))`;
      }
      updateOriginalEvent() {
          if (this.lastDaysDiff === 0)
              return;
          updateDraggedEvent(this.$app, this.eventCopy, this.originalStart);
      }
  }

  const calculateDaysDifference = (startDate, endDate) => {
      const { year: sYear, month: sMonth, date: sDate } = toIntegers(startDate);
      const { year: eYear, month: eMonth, date: eDate } = toIntegers(endDate);
      const startDateObj = new Date(sYear, sMonth, sDate);
      const endDateObj = new Date(eYear, eMonth, eDate);
      const timeDifference = endDateObj.getTime() - startDateObj.getTime();
      return Math.round(timeDifference / (1000 * 3600 * 24));
  };

  var WeekDay;
  (function (WeekDay) {
      WeekDay[WeekDay["SUNDAY"] = 0] = "SUNDAY";
      WeekDay[WeekDay["MONDAY"] = 1] = "MONDAY";
      WeekDay[WeekDay["TUESDAY"] = 2] = "TUESDAY";
      WeekDay[WeekDay["WEDNESDAY"] = 3] = "WEDNESDAY";
      WeekDay[WeekDay["THURSDAY"] = 4] = "THURSDAY";
      WeekDay[WeekDay["FRIDAY"] = 5] = "FRIDAY";
      WeekDay[WeekDay["SATURDAY"] = 6] = "SATURDAY";
  })(WeekDay || (WeekDay = {}));

  WeekDay.MONDAY;
  const DEFAULT_EVENT_COLOR_NAME = 'primary';

  class CalendarEventImpl {
      constructor(_config, id, start, end, title, people, location, description, calendarId, _options = undefined, _customContent = {}, _foreignProperties = {}) {
          Object.defineProperty(this, "_config", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: _config
          });
          Object.defineProperty(this, "id", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: id
          });
          Object.defineProperty(this, "start", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: start
          });
          Object.defineProperty(this, "end", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: end
          });
          Object.defineProperty(this, "title", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: title
          });
          Object.defineProperty(this, "people", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: people
          });
          Object.defineProperty(this, "location", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: location
          });
          Object.defineProperty(this, "description", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: description
          });
          Object.defineProperty(this, "calendarId", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: calendarId
          });
          Object.defineProperty(this, "_options", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: _options
          });
          Object.defineProperty(this, "_customContent", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: _customContent
          });
          Object.defineProperty(this, "_foreignProperties", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: _foreignProperties
          });
          Object.defineProperty(this, "_previousConcurrentEvents", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: void 0
          });
          Object.defineProperty(this, "_totalConcurrentEvents", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: void 0
          });
          Object.defineProperty(this, "_maxConcurrentEvents", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: void 0
          });
          Object.defineProperty(this, "_nDaysInGrid", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: void 0
          });
          Object.defineProperty(this, "_createdAt", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: void 0
          });
          Object.defineProperty(this, "_eventFragments", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: {}
          });
      }
      get _isSingleDayTimed() {
          return (dateTimeStringRegex.test(this.start) &&
              dateTimeStringRegex.test(this.end) &&
              dateFromDateTime(this.start) === dateFromDateTime(this.end));
      }
      get _isSingleDayFullDay() {
          return (dateStringRegex.test(this.start) &&
              dateStringRegex.test(this.end) &&
              this.start === this.end);
      }
      get _isMultiDayTimed() {
          return (dateTimeStringRegex.test(this.start) &&
              dateTimeStringRegex.test(this.end) &&
              dateFromDateTime(this.start) !== dateFromDateTime(this.end));
      }
      get _isMultiDayFullDay() {
          return (dateStringRegex.test(this.start) &&
              dateStringRegex.test(this.end) &&
              this.start !== this.end);
      }
      get _isSingleHybridDayTimed() {
          if (!this._config.isHybridDay)
              return false;
          if (!dateTimeStringRegex.test(this.start) ||
              !dateTimeStringRegex.test(this.end))
              return false;
          const startDate = dateFromDateTime(this.start);
          const endDate = dateFromDateTime(this.end);
          const endDateMinusOneDay = toDateString(new Date(toJSDate(endDate).getTime() - 86400000));
          if (startDate !== endDate && startDate !== endDateMinusOneDay)
              return false;
          const dayBoundaries = this._config.dayBoundaries.value;
          const eventStartTimePoints = timePointsFromString(timeFromDateTime(this.start));
          const eventEndTimePoints = timePointsFromString(timeFromDateTime(this.end));
          return ((eventStartTimePoints >= dayBoundaries.start &&
              (eventEndTimePoints <= dayBoundaries.end ||
                  eventEndTimePoints > eventStartTimePoints)) ||
              (eventStartTimePoints < dayBoundaries.end &&
                  eventEndTimePoints <= dayBoundaries.end));
      }
      get _color() {
          if (this.calendarId &&
              this._config.calendars.value &&
              this.calendarId in this._config.calendars.value) {
              return this._config.calendars.value[this.calendarId].colorName;
          }
          return DEFAULT_EVENT_COLOR_NAME;
      }
      _getForeignProperties() {
          return this._foreignProperties;
      }
      _getExternalEvent() {
          return {
              id: this.id,
              start: this.start,
              end: this.end,
              title: this.title,
              people: this.people,
              location: this.location,
              description: this.description,
              calendarId: this.calendarId,
              _options: this._options,
              ...this._getForeignProperties(),
          };
      }
  }

  class CalendarEventBuilder {
      constructor(_config, id, start, end) {
          Object.defineProperty(this, "_config", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: _config
          });
          Object.defineProperty(this, "id", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: id
          });
          Object.defineProperty(this, "start", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: start
          });
          Object.defineProperty(this, "end", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: end
          });
          Object.defineProperty(this, "people", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: void 0
          });
          Object.defineProperty(this, "location", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: void 0
          });
          Object.defineProperty(this, "description", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: void 0
          });
          Object.defineProperty(this, "title", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: void 0
          });
          Object.defineProperty(this, "calendarId", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: void 0
          });
          Object.defineProperty(this, "_foreignProperties", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: {}
          });
          Object.defineProperty(this, "_options", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: undefined
          });
          Object.defineProperty(this, "_customContent", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: {}
          });
      }
      build() {
          return new CalendarEventImpl(this._config, this.id, this.start, this.end, this.title, this.people, this.location, this.description, this.calendarId, this._options, this._customContent, this._foreignProperties);
      }
      withTitle(title) {
          this.title = title;
          return this;
      }
      withPeople(people) {
          this.people = people;
          return this;
      }
      withLocation(location) {
          this.location = location;
          return this;
      }
      withDescription(description) {
          this.description = description;
          return this;
      }
      withForeignProperties(foreignProperties) {
          this._foreignProperties = foreignProperties;
          return this;
      }
      withCalendarId(calendarId) {
          this.calendarId = calendarId;
          return this;
      }
      withOptions(options) {
          this._options = options;
          return this;
      }
      withCustomContent(customContent) {
          this._customContent = customContent;
          return this;
      }
  }

  const deepCloneEvent = (calendarEvent, $app) => {
      const calendarEventInternal = new CalendarEventBuilder($app.config, calendarEvent.id, calendarEvent.start, calendarEvent.end)
          .withTitle(calendarEvent.title)
          .withPeople(calendarEvent.people)
          .withCalendarId(calendarEvent.calendarId)
          .withForeignProperties(JSON.parse(JSON.stringify(calendarEvent._getForeignProperties())))
          .withLocation(calendarEvent.location)
          .withDescription(calendarEvent.description)
          .withOptions(calendarEvent._options)
          .withCustomContent(calendarEvent._customContent)
          .build();
      calendarEventInternal._nDaysInGrid = calendarEvent._nDaysInGrid;
      return calendarEventInternal;
  };

  class MonthGridDragHandlerImpl {
      constructor(calendarEvent, $app) {
          Object.defineProperty(this, "calendarEvent", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: calendarEvent
          });
          Object.defineProperty(this, "$app", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: $app
          });
          Object.defineProperty(this, "allDayElements", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: void 0
          });
          Object.defineProperty(this, "currentDragoverDate", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: void 0
          });
          Object.defineProperty(this, "eventNDays", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: void 0
          });
          Object.defineProperty(this, "originalStart", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: void 0
          });
          Object.defineProperty(this, "originalEnd", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: void 0
          });
          Object.defineProperty(this, "MONTH_DAY_CLASS_NAME", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: 'sx__month-grid-day'
          });
          Object.defineProperty(this, "MONTH_DAY_SELECTOR", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: `.${this.MONTH_DAY_CLASS_NAME}`
          });
          Object.defineProperty(this, "DAY_DRAGOVER_CLASS_NAME", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: 'sx__month-grid-day--dragover'
          });
          Object.defineProperty(this, "handleDragOver", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: (e) => {
                  e.preventDefault();
                  let dayElement = e.target;
                  if (!(dayElement instanceof HTMLDivElement) ||
                      !dayElement.classList.contains(this.MONTH_DAY_CLASS_NAME))
                      dayElement = e.target.closest(this.MONTH_DAY_SELECTOR);
                  if (this.currentDragoverDate === dayElement.dataset.date)
                      return;
                  this.currentDragoverDate = dayElement.dataset.date;
                  const newEndDate = addDays(this.currentDragoverDate, this.eventNDays - 1);
                  this.allDayElements.forEach((el) => {
                      const dayElementDate = el.dataset.date;
                      if (dayElementDate >= this.currentDragoverDate &&
                          dayElementDate <= newEndDate) {
                          el.classList.add(this.DAY_DRAGOVER_CLASS_NAME);
                      }
                      else {
                          el.classList.remove(this.DAY_DRAGOVER_CLASS_NAME);
                      }
                  });
              }
          });
          Object.defineProperty(this, "handleDragEnd", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: () => {
                  this.allDayElements.forEach((el) => {
                      el.removeEventListener('dragover', this.handleDragOver);
                      el.classList.remove(this.DAY_DRAGOVER_CLASS_NAME);
                  });
                  this.setCalendarEventPointerEventsTo('auto');
                  const updatedEvent = this.createUpdatedEvent();
                  const shouldAbort = testIfShouldAbort(this.$app, updatedEvent, this.originalStart, this.originalEnd);
                  if (shouldAbort)
                      return;
                  this.updateCalendarEvent(updatedEvent);
              }
          });
          Object.defineProperty(this, "setCalendarEventPointerEventsTo", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: (pointerEvents) => {
                  var _a;
                  ((_a = this.$app.elements.calendarWrapper) === null || _a === void 0 ? void 0 : _a.querySelectorAll('.sx__event')).forEach((el) => {
                      if (String(el.dataset.eventId) === String(this.calendarEvent.id))
                          return;
                      el.style.pointerEvents = pointerEvents;
                  });
              }
          });
          Object.defineProperty(this, "createUpdatedEvent", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: () => {
                  const eventCopy = deepCloneEvent(this.calendarEvent, this.$app);
                  const diffOldDateAndNewDate = calculateDaysDifference(dateFromDateTime(this.calendarEvent.start), dateFromDateTime(this.currentDragoverDate));
                  eventCopy.start = addDays(eventCopy.start, diffOldDateAndNewDate);
                  eventCopy.end = addDays(eventCopy.end, diffOldDateAndNewDate);
                  return eventCopy;
              }
          });
          Object.defineProperty(this, "updateCalendarEvent", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: (newEvent) => {
                  updateDraggedEvent(this.$app, newEvent, this.originalStart);
              }
          });
          this.originalStart = this.calendarEvent.start;
          this.originalEnd = this.calendarEvent.end;
          this.allDayElements = $app.elements.calendarWrapper.querySelectorAll(this.MONTH_DAY_SELECTOR);
          this.eventNDays =
              calculateDaysDifference(this.calendarEvent.start, this.calendarEvent.end) + 1;
          this.init();
      }
      init() {
          document.addEventListener('dragend', this.handleDragEnd, { once: true });
          this.allDayElements.forEach((el) => {
              el.addEventListener('dragover', this.handleDragOver);
          });
          this.setCalendarEventPointerEventsTo('none');
      }
  }

  const definePlugin = (name, definition) => {
      definition.name = name;
      return definition;
  };

  class DragAndDropPluginImpl {
      onRender($app) {
          if (!$app.elements.calendarWrapper)
              return;
          $app.elements.calendarWrapper.dataset.hasDnd = 'true';
      }
      constructor(minutesPerInterval) {
          Object.defineProperty(this, "minutesPerInterval", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: minutesPerInterval
          });
          Object.defineProperty(this, "name", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: PluginName.DragAndDrop
          });
      }
      createTimeGridDragHandler(dependencies, dayBoundariesDateTime) {
          return new TimeGridDragHandlerImpl(dependencies.$app, dependencies.eventCoordinates, dependencies.eventCopy, dependencies.updateCopy, dayBoundariesDateTime, this.getTimePointsForIntervalConfig());
      }
      getTimePointsForIntervalConfig() {
          if (this.minutesPerInterval === 60)
              return 100;
          if (this.minutesPerInterval === 30)
              return 50;
          return 25;
      }
      createDateGridDragHandler(dependencies) {
          return new DateGridDragHandlerImpl(dependencies.$app, dependencies.eventCoordinates, dependencies.eventCopy, dependencies.updateCopy);
      }
      createMonthGridDragHandler(calendarEvent, $app) {
          return new MonthGridDragHandlerImpl(calendarEvent, $app);
      }
  }
  const createDragAndDropPlugin = (minutesPerInterval = 15) => {
      return definePlugin('dragAndDrop', new DragAndDropPluginImpl(minutesPerInterval));
  };

  exports.createDragAndDropPlugin = createDragAndDropPlugin;

}));
