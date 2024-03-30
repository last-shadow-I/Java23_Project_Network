//import api from "./api.js";

document.addEventListener("DOMContentLoaded", () => {
    const table_network = document.getElementById("table-network-information");
    const table_host = document.getElementById("table-unreachable-hosts");

    const loadingSpinner = document.getElementById("loading");
    const alert = document.getElementById("alert");

    let network_info = [];
    let hosts = [];

    loadInfo();

    function loadInfo() {
        setLoading(loadingSpinner, true);
        setAlert(alert);
        table_network.hidden = true;
        table_host.hidden = true;

        Promise.all([
            api.getNetwork(),
            api.getHostsCount(),
            api.getLinesCount(),
            api.getUnreachableHosts()
        ])
            .then(([network, hostsCount, linesCount, unreachableHostsMock]) => {
                network_info = network;
                network_info[0].hostsCount = hostsCount;
                network_info[0].linesCount = linesCount;

                hosts = unreachableHostsMock.map((i) => ({
                    ip_address: i.ip_address,
                    mac_address: i.mac_address,
                }));
                showTable();
                table_network.hidden = false;
                table_host.hidden = false;
            })
            .catch((err) => {
                console.error("getNetwork failed", err);
                setAlert(alert, "Произошла ошибка при загрузке информации");
            })
            .finally(() => setLoading(loadingSpinner, false));
    }

    function showTable() {
        setRows(table_network, network_info);
        setRows(table_host, hosts);
    }

});