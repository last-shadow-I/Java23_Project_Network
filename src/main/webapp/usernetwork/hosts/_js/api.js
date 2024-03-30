const api = {

    async getHosts(filterBy) {

        let hosts = HostsMock.hosts;
        let filterHosts = hosts;
        if (filterBy) {
            filterHosts = hosts.filter(
                (i) => i.ip_address == filterBy || i.mac_address.match(new RegExp(filterBy, "gi"))
            );
        }
        let answer = await this.getAnswer(filterHosts, hosts);
        let hostsLinkedDirectly = await this.gethostsLinkedDirectly(filterHosts, hosts);
        let availableHosts = await this.getAvailableHosts(filterHosts, hosts);
        return [filterHosts.map(e => [e.ip_address, e.mac_address]), 
        answer, 
        hostsLinkedDirectly.map(e => [e.ip_address, e.mac_address]), 
        availableHosts.map(e => [e.ip_address, e.mac_address])];
    },

    async getAnswer(filterHosts, hosts) {
        let indexs = [];
        let allHostsId;

        allHostsId = hosts.map(el => el.id);
        for (const element of filterHosts) {
            let index = allHostsId.indexOf(element.id);
            if (index != -1) {
                indexs.push(index);
            }
        }
        let answer = [];
        indexs.forEach(element => {
            answer.push(trueFalseMock.answer[element]);
        });
        return answer;
    },


    async gethostsLinkedDirectly(filterHosts, hosts) {
        let indexs = [];
        let allHostsId;

        allHostsId = hosts.map(el => el.id);
        for (const element of filterHosts) {
            let index = allHostsId.indexOf(element.id);
            if (index != -1) {
                indexs.push(index);
            }
        }
        let answer = [];
        indexs.forEach(element => {
            answer.push(hostsLinkedDirectlyMock.hosts[2 * element]);
            answer.push(hostsLinkedDirectlyMock.hosts[2 * element + 1]);
        });
        return answer;
    },

    async getAvailableHosts(filterHosts, hosts) {
        let indexs = [];
        let allHostsId;

        allHostsId = hosts.map(el => el.id);
        for (const element of filterHosts) {
            let index = allHostsId.indexOf(element.id);
            if (index != -1) {
                indexs.push(index);
            }
        }
        let answer = [];
        indexs.forEach(element => {
            answer.push(availableHostsMock.hosts[2 * element]);
            answer.push(availableHostsMock.hosts[2 * element + 1]);
        });
        return answer;
    }
};