function higherLowerUrbanPrices( )
%% Create a double plot with the highest and lowest prices for urban cells

cd('../General');
H = load('maxUrbanPrice.txt');
F = grpstats(H, [H(:,1)]);
H = load('minUrbanPrice.txt');
L = grpstats(H, [H(:,1)]);
steps = [1:1:600];

hold on;
h=plot(steps, F(2,2:end),steps, L(1,2:end))
set (h, 'LineWidth', 2)
xlabel('time steps','fontweight','bold','fontsize',14)
ylabel('Urban Prices','fontweight','bold','fontsize',14)
title('Maximum & Minimum Urban prices','fontweight','bold','fontsize',16)
legend('max','min','Location','NorthWest')
handle=gcf;
saveas(handle, 'maxminUrbanPrices','epsc')
hold off;
cd('../Functions');
end

