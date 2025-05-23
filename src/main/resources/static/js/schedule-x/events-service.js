(function (global, factory) {
  typeof exports === 'object' && typeof module !== 'undefined' ? factory(exports) :
  typeof define === 'function' && define.amd ? define(['exports'], factory) :
  (global = typeof globalThis !== 'undefined' ? globalThis : global || self, factory(global.SXEventsService = {}));
})(this, (function (exports) { 'use strict';

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

  class InvalidTimeStringError extends Error {
      constructor(timeString) {
          super(`Invalid time string: ${timeString}`);
      }
  }

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

  const dateFromDateTime = (dateTime) => {
      return dateTime.slice(0, 10);
  };
  const timeFromDateTime = (dateTime) => {
      return dateTime.slice(11);
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

  const externalEventToInternal = (event, config) => {
      const { id, start, end, title, description, location, people, _options, ...foreignProperties } = event;
      return new CalendarEventBuilder(config, id, start, end)
          .withTitle(title)
          .withDescription(description)
          .withLocation(location)
          .withPeople(people)
          .withCalendarId(event.calendarId)
          .withOptions(_options)
          .withForeignProperties(foreignProperties)
          .withCustomContent(event._customContent)
          .build();
  };

  class EventsFacadeImpl {
      constructor($app) {
          Object.defineProperty(this, "$app", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: $app
          });
      }
      set(events) {
          this.$app.calendarEvents.list.value = events.map((event) => externalEventToInternal(event, this.$app.config));
      }
      add(event) {
          const newEvent = externalEventToInternal(event, this.$app.config);
          newEvent._createdAt = new Date();
          const copiedEvents = [...this.$app.calendarEvents.list.value];
          copiedEvents.push(newEvent);
          this.$app.calendarEvents.list.value = copiedEvents;
      }
      get(id) {
          var _a;
          return (_a = this.$app.calendarEvents.list.value
              .find((event) => event.id === id)) === null || _a === void 0 ? void 0 : _a._getExternalEvent();
      }
      getAll() {
          return this.$app.calendarEvents.list.value.map((event) => event._getExternalEvent());
      }
      remove(id) {
          const index = this.$app.calendarEvents.list.value.findIndex((event) => event.id === id);
          const copiedEvents = [...this.$app.calendarEvents.list.value];
          copiedEvents.splice(index, 1);
          this.$app.calendarEvents.list.value = copiedEvents;
      }
      update(event) {
          const index = this.$app.calendarEvents.list.value.findIndex((e) => e.id === event.id);
          const copiedEvents = [...this.$app.calendarEvents.list.value];
          copiedEvents.splice(index, 1, externalEventToInternal(event, this.$app.config));
          this.$app.calendarEvents.list.value = copiedEvents;
      }
  }

  const definePlugin = (name, definition) => {
      definition.name = name;
      return definition;
  };

  const validateEvents = (events = []) => {
      events === null || events === void 0 ? void 0 : events.forEach((event) => {
          if (!dateTimeStringRegex.test(event.start) &&
              !dateStringRegex.test(event.start)) {
              throw new Error(`[Schedule-X error]: Event start time ${event.start} is not a valid time format. Please refer to the docs for more information.`);
          }
          if (!dateTimeStringRegex.test(event.end) &&
              !dateStringRegex.test(event.end)) {
              throw new Error(`[Schedule-X error]: Event end time ${event.end} is not a valid time format. Please refer to the docs for more information.`);
          }
          const isIdDecimalNumber = typeof event.id === 'number' && event.id % 1 !== 0;
          if (isIdDecimalNumber) {
              throw new Error(`[Schedule-X error]: Event id ${event.id} is not a valid id. Only non-unicode characters that can be used by document.querySelector is allowed, see: https://developer.mozilla.org/en-US/docs/Web/CSS/ident. We recommend using uuids or integers.`);
          }
          // only allow non-unicode characters that can be used by document.querySelector: https://developer.mozilla.org/en-US/docs/Web/CSS/ident
          if (typeof event.id === 'string' && !/^[a-zA-Z0-9_-]*$/.test(event.id)) {
              throw new Error(`[Schedule-X error]: Event id ${event.id} is not a valid id. Only non-unicode characters that can be used by document.querySelector is allowed, see: https://developer.mozilla.org/en-US/docs/Web/CSS/ident. We recommend using uuids or integers.`);
          }
          if (typeof event.id !== 'string' && typeof event.id !== 'number') {
              throw new Error(`[Schedule-X error]: Event id ${event.id} is not a valid id. Only non-unicode characters that can be used by document.querySelector is allowed, see: https://developer.mozilla.org/en-US/docs/Web/CSS/ident. We recommend using uuids or integers.`);
          }
      });
  };

  class EventsServicePluginImpl {
      constructor() {
          Object.defineProperty(this, "name", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: 'EventsServicePlugin'
          });
          Object.defineProperty(this, "$app", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: void 0
          });
          Object.defineProperty(this, "eventsFacade", {
              enumerable: true,
              configurable: true,
              writable: true,
              value: void 0
          });
      }
      beforeRender($app) {
          this.$app = $app;
          // TODO v3: move methods from events facade to here, and remove events facade
          this.eventsFacade = new EventsFacadeImpl($app);
      }
      update(event) {
          validateEvents([event]);
          this.eventsFacade.update(event);
      }
      add(event) {
          validateEvents([event]);
          this.eventsFacade.add(event);
      }
      remove(id) {
          this.eventsFacade.remove(id);
      }
      get(id) {
          return this.eventsFacade.get(id);
      }
      getAll() {
          return this.eventsFacade.getAll();
      }
      set(events) {
          validateEvents(events);
          this.eventsFacade.set(events);
      }
      setBackgroundEvents(backgroundEvents) {
          this.$app.calendarEvents.backgroundEvents.value = backgroundEvents;
      }
  }
  const createEventsServicePlugin = () => {
      return definePlugin('eventsService', new EventsServicePluginImpl());
  };

  exports.createEventsServicePlugin = createEventsServicePlugin;

}));
