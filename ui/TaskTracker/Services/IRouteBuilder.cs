using System;

namespace TaskTracker.Services;

public interface IRouteBuilder
{
    public void AddRoute(string route, Type type);
}
