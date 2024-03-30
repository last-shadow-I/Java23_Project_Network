//import api from "./api.js";

document.addEventListener("DOMContentLoaded", () => {

    const search = document.getElementById("input-search");
    const loadingSpinner = document.getElementById("loading");
    const alert = document.getElementById("alert");
    const table = document.getElementById("table-find-hosts")
    const templateTable = document.querySelector("#template-table-hosts");

    let filterBy;

    search.addEventListener("change", (evt) => {
        setFilter(evt.target.value);
    });

    function setFilter(filter) {
        filterBy = filter;
        loadHosts();
    }

    loadHosts();

    async function loadHosts() {
        setLoading(loadingSpinner, true);
        setAlert(alert);
        table.hidden = true;

        let hosts_table = [];

        let hostsLinkedDirectly = [];

        let availableHosts = [];

        api.getHosts(filterBy)
            .then((result) => {

                for (let index = 0; index < result[0].length; index++) {
                    hosts_table[index] = result[0][index];
                    hosts_table[index].answer = result[1][index];
                    hostsLinkedDirectly[2 * index] = result[2][2 * index];
                    hostsLinkedDirectly[2 * index + 1] = result[2][2 * index + 1];
                    availableHosts[2 * index] = result[3][2 * index];
                    availableHosts[2 * index + 1] = result[3][2 * index + 1];
                }
                showTable(hosts_table, hostsLinkedDirectly, availableHosts);
                table.hidden = false;
            }).catch((err) => {
                console.error("getHosts failed", err);
                setAlert(alert, "Произошла ошибка при загрузке хостов");
            }).finally(() => setLoading(loadingSpinner, false));
    }


    (filter) => {
        filterBy = filter;
        loadHosts();
    }

    function showTable(hosts_table, hostsLinkedDirectly, availableHosts) {
        setTable(table, hosts_table, templateTable, hostsLinkedDirectly, availableHosts)
    }
});
