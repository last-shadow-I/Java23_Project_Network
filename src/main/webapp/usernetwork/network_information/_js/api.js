const api = {
    async getNetwork() {
      return networkMock.network
    },
  
    async getHostsCount() {
        return hostsCount;
    },

    async getLinesCount() {
        return linesCount;
    },

    async getUnreachableHosts() {
        return unreachableHostsMock.hosts;
    }
  };