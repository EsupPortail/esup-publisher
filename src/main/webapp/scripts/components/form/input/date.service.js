'use strict';

angular.module('publisherApp')
    .factory('DateService', function () {

        return {
            isValidDate: function (dateStr) {
                //console.log('from dateStr :', dateStr);
                //console.log('date parse dateStr ', new Date(dateStr) );
                if (dateStr == undefined)
                    return false;
                var dateTime = new Date(dateStr);

                if (isNaN(dateTime)) {
                    return false;
                }
                return true;
            },

            getDateDifference: function (fromDate, toDate) {
                //console.log('date parse toDate ', new Date(toDate) );
                //console.log('date parse fromDate ', new Date(fromDate) );
                return new Date(toDate) - new Date(fromDate);
            },

            isValidDateRange: function (fromDate, toDate) {
                //console.log('date parse toDate ', new Date(toDate) );
                //console.log('date parse fromDate ', new Date(fromDate) );
                if (fromDate == "" || toDate == "") {
                    return true;
                }
                if (this.isValidDate(fromDate) == false) {
                    //console.log('from date Not valid date');
                    return false;
                }
                if (this.isValidDate(toDate) == true) {
                    var days = this.getDateDifference(fromDate, toDate);
                    //console.log('is valid date with ', days );
                    if (days <= 0) {
                        return false;
                    }
                }
                //console.log('No valid date passed ', days );
                return true;
            },
            // normalize string dd/mm/yyyy or yyyy-mm-dd into date
            normalize: function(str) {
                if (str && !angular.isDate(str)) {
                    //console.log("normalize : ", str);
                    var match = str.match(/^(\d{1,2})\/(\d{1,2})\/(\d{4})$/);
                    if (match) {
                        //console.log("normalize dd/mm/yyyy : ", match);
                        return new Date(match[3], match[2]-1, match[1]);
                    }
                    match = str.match(/^(\d{4})\-(\d{1,2})\-(\d{1,2})$/);
                    if (match) {
                        //console.log("normalize yyyy-mm-dd : ", match);
                        return new Date(match[1], match[2]-1, match[3]);
                    }
                }
                // else {
                //    console.log("no normalization needed on date : ", str);
                //}
            }
        }
    });
