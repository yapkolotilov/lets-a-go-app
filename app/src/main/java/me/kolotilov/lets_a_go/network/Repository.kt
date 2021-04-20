package me.kolotilov.lets_a_go.network

interface Repository : LocalRepository, NetworkRepository {
}

class RepositoryImpl(
    private val localRepository: LocalRepository,
    private val networkRepository: NetworkRepository
) : Repository,
    LocalRepository by localRepository,
    NetworkRepository by networkRepository