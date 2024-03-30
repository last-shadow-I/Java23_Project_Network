//export
function autoFormat(value, format) {
    switch (format || typeof value) {
      case "string":
        return value;
      case "year":
        return value.toLocaleString("ru-RU", {
          maximumFractionDigits: 0,
          useGrouping: false,
        });
      case "integer":
        return value.toLocaleString("ru-RU", {
          maximumFractionDigits: 0,
          useGrouping: true,
        });
      case "number":
      case "currency":
        return value.toLocaleString("ru-RU", {
          minimumFractionDigits: 2,
          maximumFractionDigits: 2,
          useGrouping: true,
        });
      case "boolean":
        return value ? "Да" : "Нет";
      default:
        if (value instanceof Date) {
          return value.toLocaleString();
        }
        return value?.toString();
    }
  }
 
  //export
  function setLoading(spinner, isLoading) {
    spinner.hidden = !isLoading;
  }
  
  //export
  function setAlert(alert, message) {
    if (!message) {
      alert.hidden = true;
      return;
    }
    alert.innerText = message;
    alert.hiddent = false;
  }
  