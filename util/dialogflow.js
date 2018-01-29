let allSources = {
    agent: "agent"
}

let authorizedSources = {
    agent : "agent"
}

function isAuthorizedSource(source) {
    let keys = Object.keys(authorizedSources);
    for(let i = 0; i < keys.length; ++i) {
        if(source === Object.values(authorizedSources, keys[i])[0]) {
            return true;
        }
    }
    return false;
}

module.exports = {
    allSources : allSources,
    authorizedSources : authorizedSources,

    isAuthorizedSource : isAuthorizedSource
}