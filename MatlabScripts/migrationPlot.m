function migrationPlot()
% Create a plot with the behaviour of the migration

cd('../General');
H = load('migration.txt');
M = grpstats(H, [H(:,1)]);
steps = [1:1:600];
plot(steps, M(1,2:end), steps, M(2,2:end),steps, M(3,2:end))
hold on;
xlabel('time steps')
ylabel('migration')
title('Evolution of the migration','FontSize',12)
legend('random','GA','close','Location','NorthWest')
handle=gcf;
saveas(handle, ['migration', 'eps'],'epsc')
hold off;
cd('../Functions');
end

