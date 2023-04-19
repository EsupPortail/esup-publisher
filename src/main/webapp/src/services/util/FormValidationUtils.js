import CommonUtils from "./CommonUtils";

const FormErrorType = Object.freeze({
  REQUIRED: "required",
  MIN_LENGTH: "minlength",
  MAX_LENGTH: "maxlength",
  MIN_VALUE: "minvalue",
  MAX_VALUE: "maxvalue",
  MIN_DATE: "mindate",
  MAX_DATE: "maxdate",
  PATTERN: "pattern",
  MAX_SIZE: "maxsize",
});

// Classe utilitaire pour la gestion de la validation des champs d'un formulaire
class FormValidationUtils {
  constructor() {
    this.errors = new Map();
  }

  static getTextFieldError(val, min, max, required) {
    if (
      required &&
      (val === null ||
        val === undefined ||
        (CommonUtils.isString(val) ? val.trim() : val).length === 0)
    ) {
      return FormErrorType.REQUIRED;
    } else if (
      min &&
      val &&
      (CommonUtils.isString(val) ? val.trim() : val).length < min
    ) {
      return FormErrorType.MIN_LENGTH;
    } else if (
      max &&
      val &&
      (CommonUtils.isString(val) ? val.trim() : val).length > max
    ) {
      return FormErrorType.MAX_LENGTH;
    } else {
      return null;
    }
  }

  static getNumberFieldError(val, min, max, required) {
    if (
      required &&
      (val === null || val === undefined || !CommonUtils.isNumber(val))
    ) {
      return FormErrorType.REQUIRED;
    } else if (
      min !== null &&
      min !== undefined &&
      val !== null &&
      val !== undefined &&
      CommonUtils.isNumber(val) &&
      val < min
    ) {
      return FormErrorType.MIN_VALUE;
    } else if (
      max !== null &&
      max !== undefined &&
      val !== null &&
      val !== undefined &&
      CommonUtils.isNumber(val) &&
      val > max
    ) {
      return FormErrorType.MAX_VALUE;
    } else {
      return null;
    }
  }

  static getDateFieldError(val, min, max, required) {
    if (required && (val === null || val === undefined)) {
      return FormErrorType.REQUIRED;
    } else if (min && val && val < min) {
      return FormErrorType.MIN_DATE;
    } else if (max && val && val > max) {
      return FormErrorType.MAX_DATE;
    } else {
      return null;
    }
  }

  static getFileFieldError(val, pattern, maxSize, required) {
    if (required && (val === null || val === undefined)) {
      return FormErrorType.REQUIRED;
    } else if (pattern && val && !val.type.match(pattern)) {
      return FormErrorType.PATTERN;
    } else if (maxSize && val && val.size > maxSize) {
      return FormErrorType.MAX_SIZE;
    } else {
      return null;
    }
  }

  static getArrayFieldError(val, min, max, required) {
    if (required && (val === null || val === undefined || val.length === 0)) {
      return FormErrorType.REQUIRED;
    } else if (
      min !== null &&
      min !== undefined &&
      val !== null &&
      val !== undefined &&
      val.length < min
    ) {
      return FormErrorType.MIN_VALUE;
    } else if (
      max !== null &&
      max !== undefined &&
      val !== null &&
      val !== undefined &&
      val.length > max
    ) {
      return FormErrorType.MAX_VALUE;
    } else {
      return null;
    }
  }

  checkTextFieldValidity(fieldName, val, min, max, required) {
    this.errors.set(
      fieldName,
      FormValidationUtils.getTextFieldError(val, min, max, required)
    );
  }

  checkNumberFieldValidity(fieldName, val, min, max, required) {
    this.errors.set(
      fieldName,
      FormValidationUtils.getNumberFieldError(val, min, max, required)
    );
  }

  checkDateFieldValidity(fieldName, val, min, max, required) {
    this.errors.set(
      fieldName,
      FormValidationUtils.getDateFieldError(val, min, max, required)
    );
  }

  checkFileFieldValidity(fieldName, val, pattern, maxSize, required) {
    this.errors.set(
      fieldName,
      FormValidationUtils.getFileFieldError(val, pattern, maxSize, required)
    );
  }

  checkArrayFieldValidity(fieldName, val, min, max, required) {
    this.errors.set(
      fieldName,
      FormValidationUtils.getArrayFieldError(val, min, max, required)
    );
  }

  hasError(fieldName, error) {
    if (fieldName) {
      if (error) {
        return this.errors.get(fieldName) === error;
      } else {
        return this.errors.get(fieldName) !== null;
      }
    } else {
      if (error) {
        return Array.from(this.errors.values()).includes(error);
      } else {
        return Array.from(this.errors.values()).some((err) => err !== null);
      }
    }
  }

  clear() {
    this.errors.clear();
  }
}

export default FormValidationUtils;
export { FormValidationUtils, FormErrorType };
