//import { autoFormat } from "./helpers.js";

function createTableCell(templateTable, hostsLinkedDirectly) {
  const td = document.createElement("td");
  tableCell = templateTable.content.cloneNode(true);

  setRows(tableCell, hostsLinkedDirectly);

  const tableBody = tableCell.querySelector("tbody");
  tableBody.replaceChildren();
  for (row of hostsLinkedDirectly) {
    tableBody.append(createRow(row));
  }
  td.append(tableCell);
  return td;
}


function createRowWithTable(row, templateTable, hostsLinkedDirectly, availableHosts) {
  console.log("createRow", row, hostsLinkedDirectly, availableHosts);
  const tr = document.createElement("tr");
  for (field in row) {
    tr.append(createCell(field, row[field]));
  }
  tr.append(createTableCell(templateTable, hostsLinkedDirectly));
  tr.append(createTableCell(templateTable, availableHosts));
  return tr;
}

function setTable(table, rows, templateTable, hostsLinkedDirectly, availableHosts) {
  const tableBody = table.querySelector("tbody");
  tableBody.replaceChildren();
  for (let i = 0; i < rows.length; i++) {
    tableBody.append(createRowWithTable(
      rows[i], 
      templateTable, 
      [hostsLinkedDirectly[2*i], hostsLinkedDirectly[2*i + 1]], 
      [availableHosts[2*i], availableHosts[2*i + 1]]));
  }
}

//export
function setRows(table, rows) {
  const tableBody = table.querySelector("tbody");
  tableBody.replaceChildren();
  for (row of rows) {
    tableBody.append(createRow(row));
  }
}

function createRow(row) {
  console.log("createRow", row);
  const tr = document.createElement("tr");
  for (field in row) {
    tr.append(createCell(field, row[field]));
  }
  return tr;
}

function createCell(field, value) {
  const td = document.createElement("td");
  td.innerText = autoFormat(
    value,
    (field.match(/quantity|count/gi) && "integer") ||
    (field == "year" && "year")
  );
  return td;
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