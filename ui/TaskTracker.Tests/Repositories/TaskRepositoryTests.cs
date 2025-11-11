using TaskTracker.Repositories;
using TaskTracker.Tests.Services;

namespace TaskTracker.Tests.Repositories;

public class TaskRepositoryTests
{
    private FakeToBackSender _backSender = new FakeToBackSender();

    [Fact]
    public void RepositorySavesTaskAfterAdd()
    {
        var repository = new TaskRepository(_backSender);
    }
}